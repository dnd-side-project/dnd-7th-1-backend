package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.matrix.dto.MatrixSetDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

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
}
