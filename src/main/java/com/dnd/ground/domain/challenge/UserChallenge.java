package com.dnd.ground.domain.challenge;

import com.dnd.ground.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @description User - Challenge 간 조인 테이블
 * @author  박찬호
 * @since   2022-07-27
 * @updated 1. 회원 탈퇴 API 구현 - 삭제된 유저로 변환하기 위한 메소드 구현
 *          - 2023-05-22 박찬호
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "user_challenge")
@Entity
public class UserChallenge {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChallengeStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private ChallengeColor color;

    //Constructor
    public UserChallenge(Challenge challenge, User user, ChallengeColor color, ChallengeStatus status) {
        this.user = user;
        this.challenge = challenge;
        this.status = status;
        this.color = color;
    }

    //챌린지 상태 변경
    public void changeStatus(ChallengeStatus status) {
        this.status = status;
    }

    public void changeUser(User user) {
        this.user = user;
    }
}
