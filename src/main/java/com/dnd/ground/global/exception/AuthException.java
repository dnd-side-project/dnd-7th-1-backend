package com.dnd.ground.global.exception;

/**
 * @description 인증/인가와 관련된 예외를 처리하는 클래스
 * @author  박찬호
 * @since   2022-12-02
 * @updated 1. BaseException을 상속받아, 인증/인가와 관련한 예외를 처리하는 클래스로 사용.
 *          - 2022.12.02 박찬호
 */
public class AuthException extends BaseExceptionAbs {
    public AuthException(ExceptionCodeSet exceptionCode) {
        super(exceptionCode);
    }
}
