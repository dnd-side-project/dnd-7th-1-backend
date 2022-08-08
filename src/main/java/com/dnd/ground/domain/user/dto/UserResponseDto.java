package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.matrix.dto.MatrixSetDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description 유저 Response Dto
 *              1. 유저(나) 매트릭스 및 정보
 *              2. 나와 챌린지 안하는 친구들 매트릭스
 *              3. 나와 챌린지 하는 친구들 매트릭스 및 정보
 *              4. 누적 칸의 수에 대한 랭킹 정보
 *  *           5. 누적 영역의 수에 대한 랭킹 정보
 * @author  박세헌
 * @since   2022-08-08
 * @updated 2022-08-09 / 랭킹에 관한 dto 생성 : 박세헌
 */

@Data
public class UserResponseDto {

    @AllArgsConstructor
    static public class UserMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        public String nickname;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        public Set<MatrixSetDto> matrices = new HashSet<>();
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
        public Set<MatrixSetDto> matrices = new HashSet<>();
    }

    @Data
    @AllArgsConstructor
    public static class matrixRanking{
        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "누적 칸의 수", example = "누적 칸의 수", required = true)
        private Integer matrixNumber;
    }

    @Data
    @AllArgsConstructor
    public static class areaRanking{
        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "누적 영역의 수", example = "누적 영역의 수", required = true)
        private Integer areaNumber;
    }
}
