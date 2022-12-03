package com.dnd.ground.global.exception;

/**
 * @description 일반적인 예외를 처리하는 클래스
 * @author  박찬호
 * @since   2022-12-03
 * @updated 1. BaseException을 상속받아, 일반적인 예외를 처리하는 클래스로 사용.
 *          - 2022.12.03 박찬호
 */
public class CommonException extends BaseExceptionAbs {
    public CommonException(ExceptionCodeSet exceptionCode) {
        super(exceptionCode);
    }
}
