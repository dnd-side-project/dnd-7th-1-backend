package com.dnd.ground.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description 올바르지 않은 요청에 대한 예외 처리
 * @author  박찬호
 * @since   2022-08-25
 * @updated 1. 커스텀 예외 생성
 *          - 2022.08.25 박찬호
 */

@Getter
@RequiredArgsConstructor

public class CNotValidationException extends RuntimeException {
    private final ErrorCode errorCode;
}
