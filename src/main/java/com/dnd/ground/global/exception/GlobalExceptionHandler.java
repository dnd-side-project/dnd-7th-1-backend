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
 * @updated 1. CNotValidationException.class 예외 처리 추가
 *          2. CExceedChallengeException.class 예외 처리 추가
 *          3. 로그에 기록할 내용 변경
 *          - 2022.08.25 박찬호
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /*--공통 예외 처리--*/
    //조회되지 않을 때 발생하는 예외 처리(Optional)
    @ExceptionHandler({CNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(CNotFoundException e) {
        log.warn("**NotFound 예외 발생** 에러 코드:{} | 내용:{}", e.getErrorCode(), e.getErrorCode().getMessage());
        return handleExceptionInternal(e.getErrorCode());
    }

    //유효하지 않은 요청에 대한 예외 처리
    @ExceptionHandler({CNotValidationException.class})
    public ResponseEntity<Object> handleNotValidationException(CNotValidationException e) {
        log.warn("**NotValidation 예외 발생** 에러 코드:{} | 내용:{}", e.getErrorCode(), e.getErrorCode().getMessage());
        return handleExceptionInternal(e.getErrorCode());
    }

    //더미 데이터로 인한 시퀀스 관련 무결성 예외 처리
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<Object> handleSqlIntegrityException(SQLIntegrityConstraintViolationException e) {
        log.warn("**SQL 무결성 예외 발생** 내용:{}", e.getMessage());
        return handleExceptionInternal(CommonErrorCode.SQL_INTEGRITY_ERROR);
    }

    //NPE 예외 처리
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<Object> handleNullPointerException(NullPointerException e) {
        log.warn("**NPE 발생** 내용:{}", e.getMessage());
        return handleExceptionInternal(CommonErrorCode.NULL_POINTER_ERROR);
    }

    //그 외 예외 처리
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(Exception e) {
        log.warn("**예외 발생** 메시지:{} | 클래스:{} | 로그:{}", e.getMessage(), e.getClass(), e.getStackTrace());
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
    
    /*--챌린지 관련 예외 처리--*/
    //챌린지 개수 초과에 대한 예외 처리
    @ExceptionHandler({CExceedChallengeException.class})
    public ResponseEntity<Object> handleExceedChallengeException(CExceedChallengeException e) {
        log.warn("**챌린지 개수 초과 예외 발생** 에러 코드:{} | 내용:{}", e.getErrorCode(), e.getErrorCode().getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .nickname(e.getNickname())
                        .build());
    }
}