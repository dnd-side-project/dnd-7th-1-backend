package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.user.User;
import lombok.Getter;

/**
 * @author 박찬호
 * @description 챌린지 조회를 위한 조건 클래스
 * @since 2023-03-01
 * @updated 1.클래스 생성
 *          2023-03-01 박찬호
 */

@Getter
public class ChallengeCond {
    private User user;
    private ChallengeStatus status;
    private byte[] uuid;

    public ChallengeCond(User user, ChallengeStatus status) {
        this.user = user;
        this.status = status;
    }

    public ChallengeCond(User user, ChallengeStatus status, byte[] uuid) {
        this.user = user;
        this.status = status;
        this.uuid = uuid;
    }
}
