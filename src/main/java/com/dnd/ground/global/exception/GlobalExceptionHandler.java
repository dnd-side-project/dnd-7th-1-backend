package com.dnd.ground.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @description 전역 예외 처리를 위한 Advice 클래스
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1. 클래스 생성
 *          - 2022.08.24 박찬호
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //조회되지 않을 때 발생하는 예외처리(Optional)
    @ExceptionHandler({CNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(CNotFoundException e) {
        log.warn("**NotFound 예외 발생**");
        return handleExceptionInternal(e.getErrorCode());
    }

    //더미 데이터로 인한 시퀀스 관련 무결성 예외 처리
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<Object> handleSqlIntegrityException() {
        log.warn("**SQL 무결성 예외 발생**");
        return handleExceptionInternal(CommonErrorCode.SQL_INTEGRITY_ERROR);
    }

    //NPE 예외 처리
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<Object> handleNullPointerException() {
        log.warn("**NPE 발생**");
        return handleExceptionInternal(CommonErrorCode.NULL_POINTER_ERROR);
    }

    //그 외 예외 처리
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException() {
        log.warn("**예외 발생**");
        return handleExceptionInternal(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    //ResponseEntity 생성
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                                .body(ErrorResponse.builder()
                                        .code(errorCode.name())
                                        .message(errorCode.getMessage())
                                        .build());
    }

}
