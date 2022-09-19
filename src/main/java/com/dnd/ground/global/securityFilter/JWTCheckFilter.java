package com.dnd.ground.global.securityFilter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.repository.UserRepository;
import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.global.exception.CNotFoundException;
import com.dnd.ground.global.exception.CommonErrorCode;
import com.dnd.ground.global.util.JwtUtil;
import com.dnd.ground.global.util.JwtVerifyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @description 매 request마다 토큰을 검사해주는 필터
 * @author  박세헌, 박찬호
 * @since   2022-08-02
 * @updated 1. 전체 적인 로직 수정
 *          - 2022.08.24 박세헌
 * @note 1. 매 request마다 토큰을 검사하여 securityContestHolder에 채워줌
 *       2. 해당 필터에서 자동 로그인을 구현 하면 될 것 같음
 */

@Slf4j
public class JWTCheckFilter extends BasicAuthenticationFilter {

    private final AuthService authService;
    private final UserRepository userRepository;

    public JWTCheckFilter(AuthenticationManager authenticationManager,
                          AuthService authService,
                          UserRepository userRepository) {
        super(authenticationManager);
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh-Token");

        JwtVerifyResult result = null;
        String token = null;

        // 리프레시 토큰이 있다면
        if (refreshToken != null) {
            try {
                token = refreshToken.substring("Bearer ".length());
                result = JwtUtil.verify(token);
            } catch (Exception e) {
                request.setAttribute("exception", CommonErrorCode.WRONG_TOKEN.getMessage());
                throw new AuthenticationException("잘못된 토큰 입니다.");
            }
            // 리프레시 토큰 만료
            if (!result.isSuccess()) {
                request.setAttribute("exception", CommonErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
                throw new TokenExpiredException("리프레시 토큰이 만료되었습니다.");
            }
            // 리프레시 토큰 유효(토큰 재발급)
            else {
                User user = userRepository.findByNickname(result.getNickname()).orElseThrow(
                        () -> new CNotFoundException(CommonErrorCode.NOT_FOUND_USER));
                // 유저의 리프레시 토큰과 넘어온 리프레시 토큰이 같으면
                if (Objects.equals(user.getRefreshToken(), token)) {
                    // 필터 통과
                    UserDetails userDetails = authService.loadUserByUsername(result.getNickname());
                    UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(userToken);
                    chain.doFilter(request, response);
                }
                else {
                    request.setAttribute("exception", CommonErrorCode.WRONG_TOKEN.getMessage());
                    throw new AuthenticationException("잘못된 토큰 입니다.");
                }
            }
        }
        else {
            // 리프레시 토큰이 없는 경우(액세스 토큰)
            // 액세스 토큰 verify
            try {
                token = accessToken.substring("Bearer ".length());
                result = JwtUtil.verify(token);
            } catch (Exception e) {
                request.setAttribute("exception", CommonErrorCode.WRONG_TOKEN.getMessage());
                throw new AuthenticationException("잘못된  토큰 입니다.");
            }
            if (!result.isSuccess()) {
                request.setAttribute("exception", CommonErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());
                throw new TokenExpiredException("액세스 토큰이 만료되었습니다.");
            }
            // 필터 통과
            UserDetails user = authService.loadUserByUsername(result.getNickname());
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword(), user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(userToken);
            chain.doFilter(request, response);
        }
    }
}
