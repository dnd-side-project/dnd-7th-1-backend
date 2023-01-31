package com.dnd.ground.global.auth.filter;

import com.dnd.ground.global.auth.UserClaim;
import com.dnd.ground.global.auth.service.AuthService;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.exception.FilterException;
import com.dnd.ground.global.util.JwtUtil;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @description 모든 API 요청에 토큰 인증
 *              리프레시 토큰이 있는 경우 토큰을 재발급함.
 * @author  박찬호
 * @since   2023-01-23
 * @updated 1. 해당 필터의 역할을 엑세스 토큰 유효성 검증 및 인증 객체 생성으로 한정
 *          - 2023.01.25 박찬호
 */

public class JWTFilter extends BasicAuthenticationFilter {
    private final AuthService authService;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private static final String BEARER = "Bearer ";

    public JWTFilter(AuthenticationManager authenticationManager,
                     AuthService authService,
                     AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager);
        this.authService = authService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessToken == null){
            onUnsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.TOKEN_EMPTY));
            return;
        }
        else accessToken = accessToken.substring(BEARER.length());

        try {
            if (JwtUtil.accessVerify(accessToken)) {
                UserClaim userClaim = authService.getUserClaim(accessToken);

                UserDetails userDetails = authService.loadUserByUsername(userClaim.getEmail());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                chain.doFilter(request, response);
            } else {
                onUnsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.ACCESS_TOKEN_EXPIRED));
            }
        } catch (AuthException e) {
            onUnsuccessfulAuthentication(request, response, new FilterException(e.getExceptionCode()));
        }
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        try {
            authenticationEntryPoint.commence(request, response, failed);
        } catch (ServletException e) {
            throw new FilterException(ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        }
    }
}
