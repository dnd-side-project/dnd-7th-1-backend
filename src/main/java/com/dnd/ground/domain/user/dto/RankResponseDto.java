package com.dnd.ground.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 랭킹 Response Dto
 * @author  박세헌
 * @since   2022-08-08
 * @updated 1. API 명세 수정
 *          - 2022.08.18 박찬호
 */

@Data
public class RankResponseDto {

    @Data
    @AllArgsConstructor
    public static class Matrix {
        @ApiModelProperty(value="누적 영역의 수를 기준 내림차순으로 유저들을 정렬", required = true)
        List<UserResponseDto.Ranking> matrixRankings;
    }

    @Data
    @AllArgsConstructor
    public static class Area {
        @ApiModelProperty(value="누적 칸의 수를 기준 내림차순으로 유저들을 정렬", required = true)
        List<UserResponseDto.Ranking> areaRankings;
    }

    @Data
    @AllArgsConstructor
    public static class Step {
        @ApiModelProperty(value="누적 걸음수를 기준 내림차순으로 유저들을 정렬", required = true)
        List<UserResponseDto.Ranking> stepRankings;
    }

}
