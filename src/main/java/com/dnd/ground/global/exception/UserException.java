package com.dnd.ground.global.exception;

/**
 * @description 회원과 관련된 예외를 처리하는 클래스
 * @author  박찬호
 * @since   2022-12-02
 * @updated 1. BaseException을 상속받아, 회원과 관련한 예외를 처리하는 클래스로 사용.
 *          - 2022.12.02 박찬호
 */
public class UserException extends BaseExceptionAbs {

    public UserException(ExceptionCodeSet exceptionCode) {
        super(exceptionCode);
    }
}
