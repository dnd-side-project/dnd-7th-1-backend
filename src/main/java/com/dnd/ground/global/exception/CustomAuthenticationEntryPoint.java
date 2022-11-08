package com.dnd.ground.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description 스프링시큐리티 JWT 예외처리
 * @author  박세헌
 * @since   2022-09-08
 * @updated 특정 접근마다 상태코드 변경
 *          - 2022-09-15 박세헌
 */

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String exception = (String) request.getAttribute("exception");

        // 서버 에러
        if (exception.equals(CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage())){
            log.info("**서버 예외 발생** 메시지:{}", CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            setResponse(response, CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 액세스 토큰 만료
        else if (exception.equals(CommonErrorCode.ACCESS_TOKEN_EXPIRED.getMessage())) {
            log.info("**토큰 만료 예외 발생** 메시지:{}", CommonErrorCode.ACCESS_TOKEN_EXPIRED.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setResponse(response, CommonErrorCode.ACCESS_TOKEN_EXPIRED);
        }

        // 리프레시 토큰 만료
        else if (exception.equals(CommonErrorCode.REFRESH_TOKEN_EXPIRED.getMessage())) {
            log.info("**토큰 만료 예외 발생** 메시지:{}", CommonErrorCode.REFRESH_TOKEN_EXPIRED.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setResponse(response, CommonErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 잘못된 토큰
        else if (exception.equals(CommonErrorCode.WRONG_TOKEN.getMessage())) {
            log.info("**토큰 예외 발생** 메시지:{}", CommonErrorCode.WRONG_TOKEN.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            setResponse(response, CommonErrorCode.WRONG_TOKEN);
        }

        // 권한 없는 모든 경우
        else {
            log.info("**권한 예외 발생** 메시지:{}", CommonErrorCode.ACCESS_DENIED.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setResponse(response, CommonErrorCode.ACCESS_DENIED);
        }
    }

    // message, code 형태로 저장
    private void setResponse(HttpServletResponse response, CommonErrorCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        JSONObject json = new JSONObject();
        json.put("message", exceptionCode.getMessage());
        json.put("code", exceptionCode);

        response.getWriter().write(String.valueOf(json));
    }
}
