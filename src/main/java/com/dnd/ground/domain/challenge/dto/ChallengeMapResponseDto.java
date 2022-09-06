package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @description 챌린지와 관련된 지도 조회 시 사용할 DTO
 * @author  박찬호
 * @since   2022-08-26
 * @updated 1.챌린지 상세보기(지도) 기능 구현
 *          - 2022.08.26 박찬호
 */

public class ChallengeMapResponseDto {

    /*챌린지 지도 상세 조회*/
    @Data @AllArgsConstructor
    public static class Detail {
        List<UserMapInfo> matrixList;
        List<UserResponseDto.Ranking> rankingList;
    }

    /*챌린지 지도에 포함되는 상세 정보*/
    @Data
    @AllArgsConstructor
    public static class UserMapInfo {
        @ApiModelProperty(value = "챌린지 색깔", example = "Red")
        private ChallengeColor color;

        @ApiModelProperty(value = "사용자의 마지막 위치(위도)", example = "37.123123")
        private Double latitude;

        @ApiModelProperty(value = "사용자의 마지막 위치(경도)", example = "127.123123")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", example = "[{\"latitude\": 37.330436, \"longitude\": -122.030216}]")
        private List<MatrixDto> matrices;
    }
}