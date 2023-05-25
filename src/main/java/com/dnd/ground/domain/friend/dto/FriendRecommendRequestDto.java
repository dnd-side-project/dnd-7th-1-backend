package com.dnd.ground.domain.friend.dto;

import com.dnd.ground.domain.matrix.dto.Location;
import com.dnd.ground.global.exception.CommonException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;


/**
 * @description 네모두 추천 친구 조회를 위한 Request DTO
 * @author  박찬호
 * @since   2023.05.16
 * @updated 1. 친구 추천 닉네임 nullable
 *          - 2023.05.25 박찬호
 */

@Getter
public class FriendRecommendRequestDto {
    public FriendRecommendRequestDto(String nickname, Double latitude, Double longitude, Double distance, Integer size) {
        if (latitude == null || longitude == null || size == null) throw new CommonException(ExceptionCodeSet.MISSING_REQUIRED_PARAM);
        this.nickname = nickname;
        this.location = new Location(latitude, longitude);
        this.distance = distance;
        this.size = size;
    }

    @ApiModelProperty(value="닉네임", example = "nickA")
    private String nickname;

    @ApiModelProperty(value="조회 위치(center)", example = "{\"latitude\":37.12345, \"longitude\":127.12345}")
    private Location location;

    @ApiModelProperty(value="다음 조회할 기준 거리(null인 경우 제일 가까운 순으로 정렬)", example = "1234.5678")
    private Double distance;

    @ApiModelProperty(value="페이지 크기", example = "5")
    private Integer size;
}
