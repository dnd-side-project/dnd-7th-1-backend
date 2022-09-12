package com.dnd.ground.global.exception;

import org.springframework.http.HttpStatus;

/**
 * @description 에러 코드 인터페이스: 각 패키지(기능) 별 예외 처리가 분리될 경우를 대비한 인터페이스 추상화
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1. 에러 코드와 관련해 필요한 메소드 구현
 *          - 2022.08.24 박찬호
 */

public interface ErrorCode {
    String name();
    HttpStatus getHttpStatus();
    String getMessage();
}
