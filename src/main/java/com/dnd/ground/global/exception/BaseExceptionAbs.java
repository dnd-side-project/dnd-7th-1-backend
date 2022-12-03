package com.dnd.ground.global.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 각 예외에 대한 공통 부분을 묶은 추상 클래스
 * @author  박찬호
 * @since   2022-12-01
 * @updated 1. 각 예외에 대한 공통 부분을 묶은 추상 클래스
 *          - 2022.12.01 박찬호
 */
@AllArgsConstructor
public class BaseExceptionAbs extends RuntimeException implements BaseException {

    private static final int STACK_TRACE_LINE_LIMIT = 3;
    private ExceptionCodeSet exceptionCode;

    @Override
    public ExceptionCodeSet getExceptionCode() {
        return this.exceptionCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.exceptionCode.getHttpStatus();
    }

    @Override
    public String getMessage() {
        return this.exceptionCode.getMessage();
    }

    @Override
    public String getCode() {
        return this.exceptionCode.getCode();
    }

    @Override
    public List<String> fewStackTrace() {
        return Arrays.stream(Arrays.copyOfRange(getStackTrace(), 0, STACK_TRACE_LINE_LIMIT))
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}
