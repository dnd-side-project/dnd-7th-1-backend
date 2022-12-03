package com.dnd.ground.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description 챌린지와 관련된 예외를 처리하는 클래스
 * @author  박찬호
 * @since   2022-08-25
 * @updated 1. BaseException을 상속받아, 챌린지와 관련한 예외를 처리하는 클래스로 사용.
 *          - 2022.12.03 박찬호
 */

@Getter
public class ChallengeException extends BaseExceptionAbs {

    public ChallengeException(ExceptionCodeSet exceptionCode, String nickname) {
        super(exceptionCode);
        this.nickname = nickname;
    }

    private final String nickname;
}
