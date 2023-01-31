package com.dnd.ground.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 인증 과정에서 발생한 예외를 처리하는 클래스
 * @author  박찬호
 * @since   2023-01-25
 * @updated 1.클래스 생성
 *          -2023.01.25 박찬호
 */

public class FilterException extends AuthenticationException implements BaseException {
    public FilterException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FilterException(String msg) {
        super(msg);
    }

    public FilterException(ExceptionCodeSet codeSet) {
        super(codeSet.getCode());
        this.code = codeSet;
    }

    private ExceptionCodeSet code;

    @Override
    public ExceptionCodeSet getExceptionCode() {
        return this.code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.code.getHttpStatus();
    }

    @Override
    public String getCode() {
        return this.code.getCode();
    }

    @Override
    public List<String> fewStackTrace() {
        return Arrays.stream(Arrays.copyOfRange(getStackTrace(), 0, 3))
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}
