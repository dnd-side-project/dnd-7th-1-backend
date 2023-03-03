package com.dnd.ground.global.exception;

import com.dnd.ground.domain.user.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 챌린지와 관련된 예외를 처리하는 클래스
 * @author  박찬호
 * @since   2022-08-25
 * @updated 1. 회원-챌린지(UC)에서 여러 명의 회원에 대한 예외 처리를 위해 필드 추가
 *          - 2023.02.17 박찬호
 */

@Getter
public class ChallengeException extends BaseExceptionAbs {

    public ChallengeException(ExceptionCodeSet exceptionCode) {
        super(exceptionCode);
    }

    public ChallengeException(ExceptionCodeSet exceptionCode, String nickname) {
        super(exceptionCode);
        this.nickname = nickname;
    }

    public ChallengeException(ExceptionCodeSet exceptionCode, List<User> userList) {
        super(exceptionCode);
        this.nicknameList = userList.stream().map(User::getNickname).collect(Collectors.toList());;
    }

    private String nickname;
    private List<String> nicknameList;
}