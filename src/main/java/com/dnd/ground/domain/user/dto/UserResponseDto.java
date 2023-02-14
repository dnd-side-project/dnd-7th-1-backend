package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.user.User;

import com.dnd.ground.domain.matrix.dto.Location;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
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
 * @updated 1.회원 영역 데이터 조회 시 일부 영역 내 데이터만 조회하도록 수정
 *          - 2023-02-14 박찬호
 */

@Data
public class UserResponseDto {

    @Data
    @AllArgsConstructor
    public static class UInfo {
        @ApiModelProperty(value = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }
    
    /*마이페이지 관련 DTO*/
    @Data @Builder
    static public class MyPage {
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

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /*유저의 프로필 관련 DTO*/
    @Data @Builder
    static public class Profile {
        @ApiModelProperty(value = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "소개 메시지", example = "소개 메시지 예시입니다.")
        private String intro;

        @ApiModelProperty(value = "유저 메일", example = "A-mail@gmail.com")
        private String mail;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /*회원의 영역 정보 관련 DTO*/
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
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
        private List<Location> matrices;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;

        //생성자
        public UserMatrix(User user) {
            this.nickname = user.getNickname();
            this.matricesNumber = 0L;

            this.latitude = 0.0;
            this.longitude = 0.0;
        }

        //수정자 모음 (프로필 사진 x)
        public void setProperties(String nickname, long matricesNumber, List<Location> matrices, Double lat, Double lon) {
            this.setNickname(nickname);
            this.setMatricesNumber(matricesNumber);
            this.setMatrices(matrices);
            this.setLatitude(lat);
            this.setLongitude(lon);
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

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
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

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
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

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
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

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /* 나의 활동 기록에서 기록이 있는 날짜 리스트 */
    @Data
    @AllArgsConstructor
    static public class dayEventList{
        @ApiModelProperty(name = "기록이 있는 날짜 리스트", example = "[2022-09-01, 2022-09-02]", dataType = "list")
        private List<LocalDate> eventList;
    }

    /* 나의 활동 기록 조회 */
    @Data @Builder
    static public class ActivityRecordResponseDto {
        @ApiModelProperty(value = "해당 날짜에 존재하는 활동 내역 정보(운동 기록 정보)")
        List<RecordResponseDto.activityRecord> activityRecords;
    }
}
