package com.dnd.ground.domain.matrix.dto;

import com.dnd.ground.domain.user.User;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

/**
 * @description 회원과 영역을 조회하기 위한 DTO
 * @author  박찬호
 * @since   2022-07-27
 * @updated 1. Point 추가 (위도, 경도에서 point로 이관 중)
 */
@Getter
@Setter
public class MatrixUserSet {
    private Location location;
    private User user;

    @QueryProjection
    public MatrixUserSet(Location locations, User user) {
        this.location = locations;
        this.user = user;
    }

}
