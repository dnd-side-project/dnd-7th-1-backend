package com.dnd.ground.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description 조회의 결과가 존재하지 않을 경우 처리할 커스텀 예외
 * @author  박찬호
 * @since   2022-08-24
 * @updated 1. 커스텀 예외 생성
 *          - 2022.08.24 박찬호
 */

@Getter
@RequiredArgsConstructor
public class CNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;
}
