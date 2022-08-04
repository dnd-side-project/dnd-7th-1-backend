package com.dnd.ground.domain.challenge;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @description 챌린지 엔티티
 * @author  박찬호
 * @since   2022-07-26
 * @updated 1.stared 컬럼 데이터 타입 변경 (LocalDateTime → LocalDate)
 *          2.UUID 컬럼 생성
 *          - 2022-08-04 박찬호
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

    @Column(columnDefinition = "CHAR(32)", nullable = false, unique = true)
    private String uuid;

    @Column(name = "challenge_name", nullable = false)
    private String name;

    @Column(name = "challenge_started", nullable = false)
    private LocalDate started;

    @Column(name = "challenge_message", nullable = false)
    private String message;

    @Column(name = "challenge_color", nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_status", nullable = false)
    @Builder.Default
    private ChallengeStatus status = ChallengeStatus.Wait;

    @OneToMany(mappedBy = "user")
    private List<UserChallenge> users = new ArrayList<>();

    @Builder(builderMethodName = "create")
    public Challenge(String name, String uuid, LocalDate started, String message, String color) {
        this.uuid = uuid;
        this.name = name;
        this.started = started;
        this.message = message;
        this.color = color;
    }
}