package com.dnd.ground.domain.challenge;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 챌린지 엔티티
 * @author  박찬호
 * @since   2022-07-26
 * @updated 1.ended 필드 추가
 *          2.started 타입 LocalDateTime으로 변경
 *          - 2023-02-27 박찬호
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name="challenge")
@Entity
public class Challenge {

    @Id @GeneratedValue
    @Column(name = "challenge_id")
    private Long id;

    @Column(columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private byte[] uuid;

    @Column(name = "challenge_name", nullable = false)
    private String name;

    @Column(name = "challenge_started", nullable = false)
    private LocalDateTime started;

    @Column(name = "challenge_ended", nullable = false)
    private LocalDateTime ended;

    @Column(name = "challenge_created", nullable = false)
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "challenge_message", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_status", nullable = false)
    @Builder.Default
    private ChallengeStatus status = ChallengeStatus.WAIT;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type", nullable = false)
    private ChallengeType type;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserChallenge> users = new ArrayList<>();

    @Builder(builderMethodName = "create")
    public Challenge(String name, byte[] uuid, LocalDateTime started, LocalDateTime ended, String message, ChallengeType type) {
        this.uuid = uuid;
        this.name = name;
        this.started = started;
        this.ended = ended;
        this.message = message;
        this.type = type;
    }

    //챌린지 상태 업데이트
    public void updateStatus(ChallengeStatus status) {
        this.status = status;
    }
}