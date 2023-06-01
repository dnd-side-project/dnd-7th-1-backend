package com.dnd.ground.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description 인증 과정에서 발생한 AuthenticationException 예외 처리
 * @author  박찬호
 * @since   2023-01-26
 * @updated 1.로그에 엔드포인트 추가
 *          - 2023.03.11 박찬호
 */

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ExceptionCodeSet exception = ExceptionCodeSet.findExceptionByCode(authException.getMessage());
        request.getRequestURL();

        //인증 실패
        if (authException.getClass().equals(BadCredentialsException.class)) {
            log.error("BadCredentialsException! msg:{} | URL:{} ", authException.getMessage(), request.getRequestURL());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            setResponse(response, ExceptionCodeSet.CREDENTIAL_FAIL);
        } else if (authException.getClass().equals(InsufficientAuthenticationException.class) || exception == null) {  //Anonymous user || 잡히지 않은 에러
            log.error("InsufficientAuthenticationException! msg:{} | URL:{} ", authException.getMessage(), request.getRequestURL());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            setResponse(response, ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        } else {
            log.error("Authentication exception! | code:{} | msg:{} | URL:{}", exception.getCode(), exception.getMessage(), request.getRequestURL());
            response.setStatus(exception.getHttpStatus().value());
            setResponse(response, exception);
        }
    }

    // message, code 형태로 저장
    private void setResponse(HttpServletResponse response, ExceptionCodeSet exceptionCode) throws IOException {
        ErrorResponse responseFormat = ErrorResponse.builder()
                .message(exceptionCode.getMessage())
                .code(exceptionCode.getCode())
                .build();

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseFormat));
    }
}
