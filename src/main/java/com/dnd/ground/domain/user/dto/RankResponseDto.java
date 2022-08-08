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
 * @updated 2022-08-09 / 랭킹에 관한 dto 생성 : 박세헌
 */

@Data
public class RankResponseDto {

    @Data
    @AllArgsConstructor
    public static class matrixRankingResponseDto{
        @ApiModelProperty(value="누적 영역의 수를 기준 내림차순으로 유저들을 정렬", required = true)
        List<UserResponseDto.matrixRanking> matrixRankings = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    public static class areaRankingResponseDto{
        @ApiModelProperty(value="누적 칸의 수를 기준 내림차순으로 유저들을 정렬", required = true)
        List<UserResponseDto.areaRanking> areaRankings = new ArrayList<>();
    }

}
