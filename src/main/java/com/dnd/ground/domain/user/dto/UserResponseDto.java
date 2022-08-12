package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.matrix.dto.MatrixSetDto;
import com.dnd.ground.domain.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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
 * @author  박세헌, 박찬호
 * @since   2022-08-08
 * @updated 1. 클래스 주석 추가
 *          2. 회원 정보 관련 이너 클래스 생성
 *          - 2022.08.12 박찬호
 */

@Data
public class UserResponseDto {

    //회원의 정보 관련 DTO (추후 프로필 사진 관련 필드 추가 예정)
    @Data @Builder
    static public class UInfo {
        private String nickname;
        private String intro;
    }



    //회원의 영역 정보 관련 DTO
    @Data
    static public class UserMatrix {
        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "현재 나의 영역", example = "77", required = true)
        private Long matricesNumber;

        @ApiModelProperty(value = "유저의 마지막 위치 - 위도", example = "마지막 위치(위도)")
        private Double latitude;

        @ApiModelProperty(value = "유저의 마지막 위치 - 경도", example = "마지막 위치(경도)")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private Set<MatrixSetDto> matrices;

        //생성자
        public UserMatrix(User user) {
            this.nickname = user.getNickname();
            this.matricesNumber = 0L;
            this.latitude = user.getLatitude();
            this.longitude = user.getLongitude();
        }

        //수정자 모음
        public void setProperties(String nickname, int matricesNumber, Set<MatrixSetDto> matrices, Double lat, Double lon) {
            this.setNickname(nickname);
            this.setMatricesNumber((long) matricesNumber);
            this.setMatrices(matrices);
            this.setLatitude(lat);
            this.setLongitude(lon);
        }
    }

    //친구의 영역 관련 DTO
    @Data @AllArgsConstructor
    static public class FriendMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickB", required = true)
        private String nickname;

        @ApiModelProperty(value = "친구의 마지막 위치 - 위도", example = "마지막 위치(위도)")
        private Double latitude;

        @ApiModelProperty(value = "친구의 마지막 위치 - 경도", example = "마지막 위치(경도)")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private Set<MatrixSetDto> matrices;

    }

    //챌린지 영역 정보 관련 DTO
    @Data
    @AllArgsConstructor
    static public class ChallengeMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickC", required = true)
        private String nickname;

        @ApiModelProperty(value = "나와 같이 하는 챌린지 개수", example = "1", required = true)
        private Integer challengeNumber;

        @ApiModelProperty(value = "지도에 나타나는 챌린지 대표 색깔", example = "#ffffff", required = true)
        private String challengeColor;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 위도", example = "마지막 위치(위도)", required = true)
        private Double latitude;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 경도", example = "마지막 위치(경도)", required = true)
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private Set<MatrixSetDto> matrices;
    }

    @Data
    @AllArgsConstructor
    public static class matrixRanking{
        @ApiModelProperty(value = "랭크", example = "1위", required = true)
        private Integer rank;

        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "누적 칸의 수", example = "누적 칸의 수", required = true)
        private Long matrixNumber;
    }

    //랭킹과 관련된 DTO
    @Data
    @AllArgsConstructor
    public static class areaRanking{
        @ApiModelProperty(value = "랭크", example = "1위", required = true)
        private Integer rank;

        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "누적 영역의 수", example = "누적 영역의 수", required = true)
        private Long areaNumber;
    }
}
