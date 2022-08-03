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
 * @updated 2022-08-03 / 챌린지 수락 여부(status) 필드 추가
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

    //Constructor
    public UserChallenge(Challenge challenge, User user) {
        this.user = user;
        this.challenge = challenge;
        this.status = ChallengeStatus.Wait;
    }

}
