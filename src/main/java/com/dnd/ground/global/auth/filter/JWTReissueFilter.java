package com.dnd.ground.global.auth.filter;

import com.dnd.ground.domain.user.LoginType;
import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.global.auth.service.AuthService;
import com.dnd.ground.global.auth.service.KakaoService;
import com.dnd.ground.global.auth.dto.JWTReissueResponseDto;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.FilterException;
import com.dnd.ground.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @description 토큰 재발급 필터
 * @author 박찬호
 * @since 2023-01-27
 * @updated 1. 새로운 필터 생성
 *          - 2023.01.27 박찬호
 */

public class JWTReissueFilter extends UsernamePasswordAuthenticationFilter {

    public JWTReissueFilter(AuthenticationManager authenticationManager,
                            KakaoService kakaoService,
                            AuthService authService,
                            AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager);
        this.kakaoService = kakaoService;
        this.authService = authService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        setFilterProcessesUrl("/reissue");
    }

    private UserClaim claim;
    private final KakaoService kakaoService;
    private final AuthService authService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BEARER = "Bearer ";
    private static final String REFRESH_TOKEN = "Refresh-Token";
    private static final String KAKAO_ACCESS_TOKEN = "KAKAO_ACCESS_TOKEN";
    private static final String KAKAO_REFRESH_TOKEN = "KAKAO_REFRESH_TOKEN";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String refreshToken = request.getHeader(REFRESH_TOKEN);
        if (refreshToken == null) throw new FilterException(ExceptionCodeSet.TOKEN_EMPTY);
        else refreshToken = refreshToken.substring(BEARER.length());

        try {
            if (JwtUtil.refreshVerify(refreshToken)) {
                claim = authService.getUserClaim(refreshToken);

                long createdMilli = UserClaim.changeCreatedFormat(claim.getCreated());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        claim.getEmail(), claim.getNickname() + createdMilli
                );
                return getAuthenticationManager().authenticate(authenticationToken);
            } else throw new FilterException(ExceptionCodeSet.REFRESH_TOKEN_INVALID);
        } catch (AuthException e) {
            throw new FilterException(e.getExceptionCode());
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException {
        String accessToken = JwtUtil.createAccessToken(claim.getEmail(), claim.getCreated());
        String refreshToken = JwtUtil.createRefreshToken(claim.getEmail(), claim.getCreated());

        JWTReissueResponseDto responseFormat = new JWTReissueResponseDto(ExceptionCodeSet.OK.getMessage(), ExceptionCodeSet.OK.getCode(), claim.getLoginType());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        //카카오 회원인 경우, 카카오 토큰 재발급
        if (claim.getLoginType().equals(LoginType.KAKAO)) {
            Map<String, String> kakaoResponse = kakaoService.reissueKakaoToken(request.getHeader(KAKAO_REFRESH_TOKEN));
            response.setHeader(KAKAO_ACCESS_TOKEN, kakaoResponse.get(KAKAO_ACCESS_TOKEN));
            response.setHeader(KAKAO_REFRESH_TOKEN, kakaoResponse.get(KAKAO_REFRESH_TOKEN));
        }
        response.setHeader(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        response.setHeader(REFRESH_TOKEN, BEARER + refreshToken);

        response.getWriter().write(objectMapper.writeValueAsString(responseFormat));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        authenticationEntryPoint.commence(request, response, failed);
    }

}
