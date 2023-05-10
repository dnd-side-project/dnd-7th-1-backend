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
 * @updated 1.영역 조회 시, 기간에 따라 조회하기 위한 생성자 추가
 *          - 2023-05-01 박찬호
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

    public MatrixCond(Location location, Double spanDelta, User user, Set<User> users, LocalDateTime started, LocalDateTime ended) {
        this.user = user;
        this.users = users;
        this.location = location;
        this.spanDelta = spanDelta;
        this.started = started;
        this.ended = ended;
    }

    public MatrixCond(byte[] uuid, Location location, Double spanDelta) {
        this.targetChallengeUuid = uuid;
        this.location = location;
        this.spanDelta = spanDelta;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
