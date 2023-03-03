package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import com.dnd.ground.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author 박찬호
 * @description 챌린지 조회를 위한 조건 클래스
 * @since 2023-03-01
 * @updated 1.챌린지 시간 관련 필드 추가
 *          2023-03-03 박찬호
 */

@Getter
public class ChallengeCond {
    private User user;
    private ChallengeStatus status;
    private LocalDateTime started;
    private LocalDateTime ended;
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

    public ChallengeCond(User user, LocalDateTime started, LocalDateTime ended) {
        this.user = user;
        this.started = started;
        this.ended = ended;
    }

    public ChallengeCond(User user) {
        this.user = user;
    }
}
