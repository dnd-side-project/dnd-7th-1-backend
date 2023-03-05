package com.dnd.ground.domain.matrix.dto;

import com.dnd.ground.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @description QueryDSL으로 영역 조회를 하기 위한 조건 클래스
 * @author  박찬호
 * @since   2023-03-03
 * @updated 1.생성자 추가
 *          - 2023-03-05 박찬호
 */


@Getter
@AllArgsConstructor
public class MatrixCond {
    private User user;
    private Location location;
    private Double spanDelta;
    private LocalDateTime started;
    private LocalDateTime ended;

    public MatrixCond(User user, LocalDateTime started, LocalDateTime ended) {
        this.user = user;
        this.started = started;
        this.ended = ended;
    }

    public MatrixCond(User user) {
        this.user = user;
    }
}
