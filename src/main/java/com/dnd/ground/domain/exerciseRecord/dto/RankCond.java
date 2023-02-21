package com.dnd.ground.domain.exerciseRecord.dto;

import com.dnd.ground.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 랭킹 조회를 위한 조건 클래스
 * @author  박찬호
 * @since   2023-02-19
 * @updated 1.클래스 생성
 *          - 2023-02-19 박찬호
 */

@Getter
@Setter
@AllArgsConstructor
public class RankCond {
    private List<User> users;
    private LocalDateTime started;
    private LocalDateTime ended;

    public RankCond(List<User> users) {
        this.users = users;
    }
}
