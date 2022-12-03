package com.dnd.ground.global.exception;

/**
 * @description 친구와 관련된 예외를 처리하는 클래스
 * @author  박찬호
 * @since   2022-12-03
 * @updated 1. BaseException을 상속받아, 친구와 관련한 예외를 처리하는 클래스로 사용.
 *          - 2022.12.03 박찬호
 */
public class FriendException extends BaseExceptionAbs {
    public FriendException(ExceptionCodeSet exceptionCode) {
        super(exceptionCode);
    }
}