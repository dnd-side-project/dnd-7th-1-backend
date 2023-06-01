package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.matrix.dto.Location;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * @description 챌린지 상세보기: 지도 API에 사용되는 Request Model
 * @author  박찬호
 * @since   2023-05-19
 * @updated 1.DTO 생성
 */

@Getter
public class ChallengeMapRequestDto {
    public ChallengeMapRequestDto(String uuid, String nickname, Double spanDelta, Double latitude, Double longitude) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.spanDelta = spanDelta;
        this.location = new Location(latitude, longitude);
    }

    @NotNull
    private String uuid;

    @NotNull
    private String nickname;

    private Double spanDelta;

    private Location location;
}
