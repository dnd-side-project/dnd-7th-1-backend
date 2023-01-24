package com.dnd.ground.global.securityFilter;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.UserClaim;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.domain.user.service.KakaoService;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.util.JwtUtil;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


/**
 * @description 모든 API 요청에 토큰 인증
 *              리프레시 토큰이 있는 경우 토큰을 재발급함.
 * @author  박찬호
 * @since   2023-01-23
 * @updated 1. 새로운 필터 생성
 *          - 2023.01.24 박찬호
 */

public class JWTFilter extends BasicAuthenticationFilter {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private static final String REFRESH_TOKEN = "Refresh-Token";
    private static final String KAKAO_ACCESS_TOKEN = "KAKAO_ACCESS_TOKEN";
    private static final String KAKAO_REFRESH_TOKEN = "KAKAO_REFRESH_TOKEN";
    private static final String BEARER = "Bearer ";
    private static final String EXCEPTION = "exception";

    public JWTFilter(AuthenticationManager authenticationManager,
                     AuthService authService,
                     UserRepository userRepository,
                     KakaoService kakaoService) {
        super(authenticationManager);
        this.authService = authService;
        this.userRepository = userRepository;
        this.kakaoService = kakaoService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String refreshToken = request.getHeader(REFRESH_TOKEN);

        //리프레시 토큰O -> 재발급
        if (refreshToken != null) {
            try {
                reissueToken(refreshToken.substring(BEARER.length()), request, response);
            } catch (AuthException e) {
                request.setAttribute(EXCEPTION, e.getExceptionCode());
            }
        }

        //엑세스 토큰 확인
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessToken == null) {
            request.setAttribute(EXCEPTION, ExceptionCodeSet.TOKEN_EMPTY);
            throw new AuthException(ExceptionCodeSet.TOKEN_EMPTY);
        }

        accessToken = accessToken.substring(BEARER.length());

        if (JwtUtil.accessVerify(accessToken)) {
            UserClaim userClaim = JwtUtil.getUserClaim(accessToken);

            UserDetails userDetails = authService.loadUserByUsername(userClaim.getEmail());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        }
    }

    private void reissueToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        if (JwtUtil.refreshVerify(refreshToken)) {
            UserClaim userClaim = JwtUtil.getUserClaim(refreshToken);

            User user = userRepository.findByEmail(userClaim.getEmail()).orElseThrow(
                    () -> new AuthException(ExceptionCodeSet.USER_NOT_FOUND));

            String accessToken = JwtUtil.createAccessToken(user.getEmail(), user.getNickname(), user.getCreated());
            String newRefreshToken = JwtUtil.createRefreshToken(user.getEmail());

            request.setAttribute(HttpHeaders.AUTHORIZATION, accessToken);
            response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
            response.setHeader(REFRESH_TOKEN, newRefreshToken);

            if (user.getLoginType().equals(LoginType.KAKAO)) {
                Map<String, String> kakaoResponse = kakaoService.reissueKakaoToken(request.getHeader(KAKAO_REFRESH_TOKEN));
                response.setHeader(KAKAO_ACCESS_TOKEN, kakaoResponse.get(KAKAO_ACCESS_TOKEN));
                response.setHeader(KAKAO_REFRESH_TOKEN, kakaoResponse.get(KAKAO_REFRESH_TOKEN));
            }
        }
    }
}
