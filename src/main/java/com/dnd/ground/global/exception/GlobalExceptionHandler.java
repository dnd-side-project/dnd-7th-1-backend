package com.dnd.ground.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 박찬호
 * @description 전역 예외 처리를 위한 Advice 클래스
 * @since 2022-08-24
 * @updated 1.예외처리 리팩토링에 따른 Response 변경
 *          2.각 패키지별 예외 처리 및 로깅 방식 변경
 *          - 2022.12.03 박찬호
 *
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 역추적을 위한 간소화된 Stack trace 계산
     */
    private List<String> getFewTrace(StackTraceElement[] trace) {
        if (trace == null) {
            log.error("Stack trace is null");
            return List.of("No stack trace");
        } else if (trace.length < 3) {
            return Arrays.stream(Arrays.copyOfRange(trace, 0, trace.length))
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList());
        } else {
            return Arrays.stream(Arrays.copyOfRange(trace, 0, 3))
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList());
        }
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
        log.error("User exception: Code:{}, Message:{}, StackTrace:{}", e.getCode(), e.getMessage(), e.fewStackTrace());
        return makeResponseFormat(e.getExceptionCode(), e.fewStackTrace());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        log.error("Auth exception: Code:{}, Message:{}, StackTrace:{}", e.getCode(), e.getMessage(), e.fewStackTrace());
        return makeResponseFormat(e.getExceptionCode(), e.fewStackTrace());
    }

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<ErrorResponse> handleFriendException(FriendException e) {
        log.error("Friend exception: Code:{}, Message:{}, StackTrace:{}", e.getCode(), e.getMessage(), e.fewStackTrace());
        return makeResponseFormat(e.getExceptionCode(), e.fewStackTrace());
    }

    @ExceptionHandler(ChallengeException.class)
    public ResponseEntity<ErrorResponse> handleChallengeException(ChallengeException e) {
        //4500 초과는 회원-챌린지 간 예외사항
        if (Integer.parseInt(e.getCode()) > 4500) {
            log.error("Challenge exceed exception: Code:{}, Message:{}, StackTrace:{}", e.getCode(), e.getMessage(), e.fewStackTrace());
            return makeUCResponseFormat(e.getExceptionCode(), e.getNickname(), e.fewStackTrace());
        } else {
            log.error("Challenge exception: Code:{}, Message:{}, StackTrace:{}", e.getCode(), e.getMessage(), e.fewStackTrace());
            return makeResponseFormat(e.getExceptionCode(), e.fewStackTrace());
        }
    }

    //Default Exception
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSqlIntegrityException(SQLIntegrityConstraintViolationException e) {
        List<String> trace = getFewTrace(e.getStackTrace());
        log.error("SQLIntegrityConstraintViolationException: Code:{}, StackTrace:{}", ExceptionCodeSet.SQL_INTEGRITY_ERROR.getCode(), trace);
        return makeResponseFormat(ExceptionCodeSet.SQL_INTEGRITY_ERROR, trace);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        List<String> trace = getFewTrace(e.getStackTrace());
        log.error("NullPointException: Code:{}, StackTrace:{}", ExceptionCodeSet.NULL_POINTER_ERROR.getCode(), trace);
        return makeResponseFormat(ExceptionCodeSet.NULL_POINTER_ERROR, trace);
    }

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientException e) {
        List<String> trace = getFewTrace(e.getStackTrace());
        log.error("WebClientException: Code:{}, StackTrace:{} | message:{}", ExceptionCodeSet.WEBCLIENT_ERROR.getCode(), trace, e.getMessage());
        return makeResponseFormat(ExceptionCodeSet.WEBCLIENT_ERROR, trace);
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<ErrorResponse> handleParseException(ParseException e) {
        List<String> trace = getFewTrace(e.getStackTrace());
        log.error("ParseException: Code:{}, StackTrace:{} | message:{}", ExceptionCodeSet.PARSE_EXCEPTION, trace, e.getMessage());
        return makeResponseFormat(ExceptionCodeSet.PARSE_EXCEPTION, trace);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        List<String> trace = getFewTrace(e.getStackTrace());
        log.error("Unexpected exception: Code:{}, StackTrace:{}", ExceptionCodeSet.INTERNAL_SERVER_ERROR.getCode(), trace);
        return makeResponseFormat(ExceptionCodeSet.INTERNAL_SERVER_ERROR, trace);
    }

    /**
     * 사전에 정의된 커스텀 예외에 대한 Response 생성
     *
     * @param exceptionCode
     */
    private ResponseEntity<ErrorResponse> makeResponseFormat(ExceptionCodeSet exceptionCode, List<String> trace) {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(exceptionCode.getCode())
                        .message(exceptionCode.getMessage())
                        .trace(trace)
                        .build()
                );
    }

    /**
     * 사전에 정의된 커스텀 예외 중, 회원-챌린지 간 예외에 대한 Response 생성
     *
     * @param exceptionCode
     * @param nickname
     */
    public ResponseEntity<ErrorResponse> makeUCResponseFormat(ExceptionCodeSet exceptionCode, String nickname, List<String> trace) {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(exceptionCode.getCode())
                        .message(exceptionCode.getMessage())
                        .nickname(nickname)
                        .trace(trace)
                        .build()
                );
    }
}