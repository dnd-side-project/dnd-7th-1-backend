package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.dto.ChallengeResponseDto;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 유저 Response Dto
 *              1. 유저(나) 매트릭스 및 정보
 *              2. 나와 챌린지 안하는 친구들 매트릭스
 *              3. 나와 챌린지 하는 친구들 매트릭스 및 정보
 *              4. 랭킹 정보
 * @author  박세헌, 박찬호
 * @since   2022-08-08
 * @updated 1. 필터에 따른 UserMatrix 수정자 생성
 *          - 2022.08.24 박찬호
 */

@Data
public class UserResponseDto {

    /*회원의 정보 관련 DTO (추후 프로필 사진 관련 필드 추가 예정)*/
    @Data @Builder
    static public class UInfo {
        @ApiModelProperty(value = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "소개 메시지", example = "소개 메시지 예시입니다.")
        private String intro;

        @ApiModelProperty(value = "이번주 채운 칸의 수", example = "9")
        private Long matrixNumber;

        @ApiModelProperty(value = "이번주 총 걸음 수", example = "1030")
        private Integer stepCount;

        @ApiModelProperty(value = "이번주 총 거리", example = "200")
        private Integer distance;

        @ApiModelProperty(value = "친구 수", example = "2")
        private Integer friendNumber;

        @ApiModelProperty(value = "역대 누적 칸 수", example = "3000")
        private Long allMatrixNumber;
    }

    /*회원 프로필 관련 DTO*/
    @Data
    @Builder
    static public class Profile {
        @ApiModelProperty(value = "친구 닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "친구의 마지막 접속 시간", example = "2022-08-18T18:10:43.78")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime lasted;

        @ApiModelProperty(value = "친구의 소개 메시지", example = "친구의 소개 메시지 예시입니다.")
        private String intro;

        @ApiModelProperty(value = "회원과 친구 관계인지 나타내는 Boolean", example = "true")
        private Boolean isFriend;

        @ApiModelProperty(value = "이번 주 영역 개수", example = "9")
        private Long areas;

        @ApiModelProperty(value = "역대 누적 칸수", example = "1030")
        private Long allMatrixNumber;

        @ApiModelProperty(value = "역대 누적 랭킹", example = "1")
        private Integer rank;

        @ApiModelProperty(value = "회원과 함께 하는 챌린지 리스트"
                , example = "[{\"name\": \"챌린지1\", \"started\": \"2022-08-16\", \"ended\": \"2022-08-21\", \"rank\": 1, \"color\": \"Red\"}]")
        List<ChallengeResponseDto.Progress> challenges;
    }

    /*회원의 영역 정보 관련 DTO*/
    @Data
    static public class UserMatrix {
        @ApiModelProperty(value = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "현재 나의 영역", example = "77")
        private Long matricesNumber;

        @ApiModelProperty(value = "유저의 마지막 위치 - 위도", example = "37.330436")
        private Double latitude;

        @ApiModelProperty(value = "유저의 마지막 위치 - 경도", example = "-122.030216")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", example = "[{\"latitude\": 37.330436, \"longitude\": -122.030216}]")
        private List<MatrixDto> matrices;

        //생성자
        public UserMatrix(User user) {
            this.nickname = user.getNickname();
            this.matricesNumber = 0L;

            this.latitude = 0.0;
            this.longitude = 0.0;
        }

        //수정자 모음
        public void setProperties(String nickname, long matricesNumber, List<MatrixDto> matrices, Double lat, Double lon) {
            this.setNickname(nickname);
            this.setMatricesNumber(matricesNumber);
            this.setMatrices(matrices);
            this.setLatitude(lat);
            this.setLongitude(lon);
        }

        public void setProperties(String nickname, long matricesNumber, Double lat, Double lon) {
            this.setNickname(nickname);
            this.setMatricesNumber(matricesNumber);
            this.setLatitude(lat);
            this.setLongitude(lon);
            this.matrices = new ArrayList<>();
        }
    }

    /*친구의 영역 관련 DTO*/
    @Data @AllArgsConstructor
    static public class FriendMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickB", required = true)
        private String nickname;

        @ApiModelProperty(value = "친구의 마지막 위치 - 위도", example = "37.330436")
        private Double latitude;

        @ApiModelProperty(value = "친구의 마지막 위치 - 경도", example = "-122.030216")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private List<MatrixDto> matrices;

    }

    /*챌린지 영역 정보 관련 DTO*/
    @Data
    @AllArgsConstructor
    static public class ChallengeMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickC", required = true)
        private String nickname;

        @ApiModelProperty(value = "나와 같이 하는 챌린지 개수", example = "1", required = true)
        private Integer challengeNumber;

        @ApiModelProperty(value = "지도에 나타나는 챌린지 대표 색깔", example = "Pink", required = true)
        private ChallengeColor challengeColor;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 위도", example = "37.123123", required = true)
        private Double latitude;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 경도", example = "127.123123", required = true)
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", example = "[{\"latitude\": 37.330436, \"longitude\": -122.030216}]",  required = true)
        private List<MatrixDto> matrices;
    }

    /*랭킹과 관련된 DTO (추후 프로필 사진 필드 추가해야됨)*/
    @Data
    @AllArgsConstructor
    public static class Ranking {
        @ApiModelProperty(value = "랭크", example = "1", required = true)
        private Integer rank;

        @ApiModelProperty(value = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(value = "점수(영역수 or 걸음수 or 역대누적칸수)", example = "50", required = true)
        private Long score;
    }

    /*상세 지도 DTO*/
    @Data
    @AllArgsConstructor
    public static class DetailMap {
        @ApiModelProperty(value = "사용자의 마지막 위치(위도)", example = "37.123123", required = true)
        private Double latitude;

        @ApiModelProperty(value = "사용자의 마지막 위치(경도)", example = "127.123123", required = true)
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", example = "[{\"latitude\": 37.330436, \"longitude\": -122.030216}]",required = true)
        private List<MatrixDto> matrices;
    }
}
