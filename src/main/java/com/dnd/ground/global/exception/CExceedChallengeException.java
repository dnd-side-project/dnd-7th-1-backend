package com.dnd.ground.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description 동시에 3개 이상의 챌린지 진행 시 예외 처리
 *              닉네임과 함께 전달하기 위해 구분.
 * @author  박찬호
 * @since   2022-08-25
 * @updated 1. 커스텀 예외 생성
 *          - 2022.08.25 박찬호
 */

@Getter
@RequiredArgsConstructor
public class CExceedChallengeException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String nickname;
}
