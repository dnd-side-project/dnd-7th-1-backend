package com.dnd.ground.domain.matrix.dto;

import com.dnd.ground.domain.matrix.MatrixUserCond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * @description 특정 영역을 조회하기 위한 Request DTO
 * @author  박찬호
 * @since   2023.03.12
 * @updated 1. 클래스 생성
 *          - 2023.03.12 박찬호
 */
@Getter
public class MatrixRequestDto {
    public MatrixRequestDto(String nickname, MatrixUserCond type, Double latitude, Double longitude, String uuid, Double spanDelta) {
        this.nickname = nickname;
        this.type = type;
        this.uuid = uuid;
        this.location = latitude == null || longitude == null ? null : new Location(latitude, longitude);
        this.spanDelta = spanDelta;
    }
    @ApiModelProperty(value="닉네임", example="nickA")
    private String nickname;
    @ApiModelProperty(value="조회 타입(ALL|CHALLENGE)", example="ALL")
    private MatrixUserCond type;
    @ApiModelProperty(value="타입이 챌린지인 경우 해당 챌린지의 UUID", example="1938as9x8c9n1kd")
    private String uuid;
    @ApiModelProperty(value="조회할 영역의 location", example="{\"latitude\":32.123,\"longitude\":123.456}")
    private Location location;
    @ApiModelProperty(value="영역의 크기 spanDelta", example="0.003")
    private Double spanDelta;
}