package com.dnd.ground.global.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @description 에러 코드 인터페이스: 각 패키지(기능) 별 예외 처리가 분리될 경우를 대비한 인터페이스 추상화
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1. 확장성을 고려한 예외 처리 리팩토링에 의한 인터페이스 재정의
 *          - 2022.12.01 박찬호
 */

public interface BaseException {
    ExceptionCodeSet getExceptionCode();
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
    List<String> fewStackTrace();
}
