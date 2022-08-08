package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.matrix.dto.MatrixSetDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * @description 홈화면 구성 Response Dto
 *              1. 유저 매트릭스 및 정보
 *              2. 챌린지 안하는 친구들 매트릭스
 *              3. 챌린지 하는 친구들 매트릭스 및 정보
 * @author  박세헌
 * @since   2022-08-02
 * @updated 2022-08-05 / 1. 홈화면 api 명세 추가
 *                       2. MatrixSetDto로 중복없는 칸 관리
 *                         - 박세헌
 */

@Data @Builder
public class HomeResponseDto {

    @ApiModelProperty(value="유저에 대한 정보", required = true)
    private UserMatrix userMatrices;

    @ApiModelProperty(value="(챌린지를 안하는)친구들에 대한 정보", required = true)
    private List<FriendMatrix> friendMatrices;

    @ApiModelProperty(value="(챌린지를 하는)유저들에 대한 정보",  required = true)
    private List<ChallengeMatrix> challengeMatrices;

    @AllArgsConstructor
    static public class UserMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        public String nickname;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        public Set<MatrixSetDto> matrices;
    }

    @AllArgsConstructor
    static public class FriendMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickB", required = true)
        public String nickname;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        public Set<MatrixSetDto> matrices;
    }

    @AllArgsConstructor
    static public class ChallengeMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickC", required = true)
        public String nickname;

        @ApiModelProperty(value = "나와 같이 하는 챌린지 개수", example = "1", required = true)
        public Integer challengeNumber;

        @ApiModelProperty(value = "지도에 나타나는 챌린지 대표 색깔", example = "#ffffff", required = true)
        public String challengeColor;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        public Set<MatrixSetDto> matrices;
    }
}
