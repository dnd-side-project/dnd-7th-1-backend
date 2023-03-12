package com.dnd.ground.domain.matrix.dto;

import com.dnd.ground.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @description QueryDSL으로 영역 조회를 하기 위한 조건 클래스
 * @author  박찬호
 * @since   2023-03-03
 * @updated 1.챌린지 영역 조회를 위한 UUID 필드 및 생성자 추가
 *          - 2023-03-12 박찬호
 */
@Getter
@Setter
@AllArgsConstructor
public class MatrixCond {
    private User user;
    private Location location;
    private Double spanDelta;
    private LocalDateTime started;
    private LocalDateTime ended;
    private Set<User> users;
    private byte[] targetChallengeUuid;

    public MatrixCond(User user, LocalDateTime started, LocalDateTime ended) {
        this.user = user;
        this.started = started;
        this.ended = ended;
    }

    public MatrixCond(User user) {
        this.user = user;
    }

    public MatrixCond(User user, Location location, Double spanDelta, LocalDateTime started, LocalDateTime ended) {
        this.user = user;
        this.location = location;
        this.spanDelta = spanDelta;
        this.started = started;
        this.ended = ended;
    }

    public MatrixCond(Set<User> users, Location location, Double spanDelta, LocalDateTime started, LocalDateTime ended) {
        this.users = users;
        this.location = location;
        this.spanDelta = spanDelta;
        this.started = started;
        this.ended = ended;
    }

    public MatrixCond(MatrixRequestDto request, User user, Set<User> users) {
        this.user = user;
        this.users = users;
        this.location = request.getLocation();
        this.spanDelta = request.getSpanDelta();
    }

    public MatrixCond(byte[] targetChallengeUuid) {
        this.targetChallengeUuid = targetChallengeUuid;
    }
}
