package com.dnd.ground.global.securityFilter;

import com.dnd.ground.domain.user.UserClaim;
import com.dnd.ground.domain.user.dto.UserSignDto;
import com.dnd.ground.domain.user.service.AuthService;
import com.dnd.ground.global.exception.AuthException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description 회원가입을 처리하는 필터.
 *              필터 레벨에서 처리하고, 서블릿 컨테이너로 접근하지 않도록 함.
 * @author  박찬호
 * @since   2023-01-23
 * @updated 1. 새로운 필터 생성
 *          - 2023.01.23 박찬호
 */

public class SignFilter extends UsernamePasswordAuthenticationFilter {

    public SignFilter(AuthenticationManager authenticationManager,
                      AuthService authService) {
        super(authenticationManager);
        this.authService = authService;
        setFilterProcessesUrl("/sign");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService;
    private UserClaim claim;
    private static final String BEARER = "Bearer ";
    private static final String EXCEPTION = "exception";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        UsernamePasswordAuthenticationToken authenticationToken = null;
        try {
            UserSignDto userSignDto = objectMapper.readValue(request.getInputStream(), UserSignDto.class);
            this.claim = authService.signUp(userSignDto);

            long createdMilli = UserClaim.changeCreatedFormat(claim.getCreated());
            authenticationToken = new UsernamePasswordAuthenticationToken(
                    claim.getEmail(), claim.getNickname() + createdMilli
            );
        } catch (IOException e) {
            request.setAttribute(EXCEPTION, ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        } catch (AuthException e) {
            request.setAttribute(EXCEPTION, e.getExceptionCode());
            throw new AuthException(e.getExceptionCode());
        }

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException {
        String accessToken = JwtUtil.createAccessToken(claim.getEmail(), claim.getNickname(), claim.getCreated());
        String refreshToken = JwtUtil.createRefreshToken(claim.getEmail());

        response.setHeader(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        response.setHeader("Refresh-Token", BEARER + refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        JSONObject json = new JSONObject();
        json.put("nickname", claim.getNickname());
        response.getWriter().write(String.valueOf(json));
    }
}