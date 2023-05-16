package com.dnd.ground.domain.friend.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

/**
 * @description 네모두 추천 친구 조회를 위한 데이터 조회 용도 DTO
 * @author  박찬호
 * @since   2023.05.16
 * @updated 1. 클래스 생성
 *          - 2023.05.16 박찬호
 */

@Getter
public class FriendRecommendPageInfo {
    private Double distance;
    private String nickname;
    private String picturePath;

    @QueryProjection
    public FriendRecommendPageInfo(Double distance, String nickname, String picturePath) {
        this.distance = distance;
        this.nickname = nickname;
        this.picturePath = picturePath;
    }
}
