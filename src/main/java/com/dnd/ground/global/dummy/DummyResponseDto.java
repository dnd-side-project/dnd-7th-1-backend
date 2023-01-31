package com.dnd.ground.global.dummy;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @description 더미 데이터 생성을 위한 DTO
 * @author  박찬호
 * @since   2022-10-04
 * @updated 1. 회원, 운동 기록, 영역과 관련된 로직 생성
 *          - 2022.10.04 박찬호
 */
public class DummyResponseDto {

    @Data @Builder
    static class DummyUser {
        @ApiModelProperty(name = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(name = "생성 시간", example = "2022-10-01T11:02")
        private LocalDateTime created;

        @ApiModelProperty(name = "소개 메시지", example = "프로필에 나오는 소개 메시지입니다.")
        private String intro;

        @ApiModelProperty(name = "이메일", example = "NickA@gmail.com")
        private String mail;

        @ApiModelProperty(name = "위도", example = "37.1234")
        private Double latitude;

        @ApiModelProperty(name = "경도", example = "122.1234")
        private Double longitude;

        @ApiModelProperty(name = "지도 필터: 나의 기록 보기", example = "true")
        private Boolean isShowMine;

        @ApiModelProperty(name = "지도 필터: 친구 보기", example = "false")
        private Boolean isShowFriend;

        @ApiModelProperty(name = "지도 필터: 내 위치 공개", example = "true")
        private Boolean isPublicRecord;

        @ApiModelProperty(name = "프로필 사진 이름", example = "user/profile/default_profile.png")
        private String pictureName;

        @ApiModelProperty(name = "프로필 사진 경로", example = "https://dnd-ground-bucket.s3.ap-northeast-2.amazonaws.com/user/profile/default_profile.png")
        private String picturePath;

        @ApiModelProperty(name = "자체 리프레시 토큰", example = "Anvi2VZamqE")
        private String refreshToken;
    }

    @Data @Builder
    static class DummyRecords {
        @ApiModelProperty(name = "운동 기록 번호", example = "1")
        private Long id;

        @ApiModelProperty(name = "운동 시작 시간", example = "2022-08-23 22:00:00")
        private LocalDateTime started;

        @ApiModelProperty(name = "운동 끝나는 시간", example = "2022-08-23 22:30:00")
        private LocalDateTime ended;

        @ApiModelProperty(name = "거리", example = "123")
        private Integer distance;

        @ApiModelProperty(name = "걸음 수", example = "564")
        private Integer stepCount;

        @ApiModelProperty(name = "운동 시간", example = "928")
        private Integer exerciseTime;

        @ApiModelProperty(name = "메시지", example = "운동 기록에 남기는 메시지")
        private String message;
    }

    @Data
    static class DummyMatricesInfo {
        @ApiModelProperty(name = "영역 개수", example = "14")
        private Long size;

        @ApiModelProperty(name = "기록될 영역 정보 | 포맷: [ {위도:경도}, {위도:경도} ]",
                example = "[[37.123123, 127.123123], [37.234234, 127.234234]]", dataType = "list", required = true)
        private ArrayList<ArrayList<Double>> matrices = new ArrayList<>();

        //영역 추가
        public void addMatrix(double latitude, double longitude) {
            ArrayList<Double> matrix = new ArrayList<>();
            matrix.add(latitude);
            matrix.add(longitude);
            this.matrices.add(matrix);
        }

    }
}
