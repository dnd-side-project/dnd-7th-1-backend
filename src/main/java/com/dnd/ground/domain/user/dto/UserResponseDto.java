package com.dnd.ground.domain.user.dto;

import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.exerciseRecord.dto.RecordResponseDto;

import com.dnd.ground.domain.matrix.dto.Location;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 유저 Response Dto
 * @author  박세헌, 박찬호
 * @since   2022-08-08
 * @updated 1. @Data 제거
 *          2. 회원의 알람 필터 조회 DTO 생성
 *          - 2023-03-07 박찬호
 */

@Getter
public class UserResponseDto {
    @Getter
    @AllArgsConstructor
    public static class UInfo {
        @ApiModelProperty(value = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }
    
    /*마이페이지 관련 DTO*/
    @Getter @Builder
    static public class MyPage {
        @ApiModelProperty(value = "닉네임", example = "NickA")
        private String nickname;

        @ApiModelProperty(value = "소개 메시지", example = "소개 메시지 예시입니다.")
        private String intro;

        @ApiModelProperty(value = "이번주 채운 칸의 수", example = "9")
        private Long matrixNumber;

        @ApiModelProperty(value = "이번주 총 걸음 수", example = "1030")
        private Long stepCount;

        @ApiModelProperty(value = "이번주 총 거리", example = "200")
        private Long distance;

        @ApiModelProperty(value = "친구 수", example = "2")
        private Integer friendNumber;

        @ApiModelProperty(value = "역대 누적 칸 수", example = "3000")
        private Long allMatrixNumber;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /*유저의 프로필 관련 DTO*/
    @Getter @Builder
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
    @ToString
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
    }

    /*친구의 영역 관련 DTO*/
    @Getter
    @Builder
    @AllArgsConstructor
    static public class FriendMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickB", required = true)
        private String nickname;

        @ApiModelProperty(value = "친구의 마지막 위치 - 위도", example = "37.330436")
        private Double latitude;

        @ApiModelProperty(value = "친구의 마지막 위치 - 경도", example = "-122.030216")
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", required = true)
        private List<Location> matrices;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /*챌린지 영역 정보 관련 DTO*/
    @Getter
    @Builder
    @AllArgsConstructor
    static public class ChallengeMatrix{
        @ApiModelProperty(value = "닉네임", example = "NickC", required = true)
        private String nickname;

        @ApiModelProperty(value = "나와 같이 하는 챌린지 개수", example = "1", required = true)
        private Long challengeNumber;

        @ApiModelProperty(value = "지도에 나타나는 챌린지 대표 색깔", example = "Pink", required = true)
        private ChallengeColor challengeColor;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 위도", example = "37.123123", required = true)
        private Double latitude;

        @ApiModelProperty(value = "챌린지를 같이 하는 사람의 마지막 위치 - 경도", example = "127.123123", required = true)
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", example = "[{\"latitude\": 37.330436, \"longitude\": -122.030216}]",  required = true)
        private List<Location> matrices;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /*랭킹과 관련된 DTO (추후 프로필 사진 필드 추가해야됨)*/
    @Getter
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
    @Getter
    @AllArgsConstructor
    public static class DetailMap {
        @ApiModelProperty(value = "사용자의 마지막 위치(위도)", example = "37.123123", required = true)
        private Double latitude;

        @ApiModelProperty(value = "사용자의 마지막 위치(경도)", example = "127.123123", required = true)
        private Double longitude;

        @ApiModelProperty(value = "칸 꼭지점 위도, 경도 리스트", example = "[{\"latitude\": 37.330436, \"longitude\": -122.030216}]",required = true)
        private List<Location> matrices;

        @ApiModelProperty(value="프로필 사진 URI(카카오 프로필 사용 시 kakao/카카오회원번호)", example="http:\\/\\/k.kakaocdn.net\\/dn\\/uQVeo\\/btrLgESJyjg\\/Pff3k36lRWkQ98ebAlexv1\\/img_640x640.jpg")
        private String picturePath;
    }

    /* 나의 활동 기록에서 기록이 있는 날짜 리스트 */
    @AllArgsConstructor
    @Getter
    static public class dayEventList{
        @ApiModelProperty(name = "기록이 있는 날짜 리스트", example = "[2022-09-01, 2022-09-02]", dataType = "list")
        private List<LocalDate> eventList;
    }

    /* 나의 활동 기록 조회 */
    @Builder
    @AllArgsConstructor
    @Getter
    static public class ActivityRecordResponseDto {
        @ApiModelProperty(value = "해당 날짜에 존재하는 활동 내역 정보(운동 기록 정보)")
        List<RecordResponseDto.activityRecord> activityRecords;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    static public class NotificationFilters {
        @ApiModelProperty(name = "회원 알람 필터: 새로운 주차 시작 알림", example = "true")
        private Boolean notiWeekStart;

        @ApiModelProperty(name = "회원 알람 필터: 주차 종료 알림", example = "false")
        private Boolean notiWeekEnd;

        @ApiModelProperty(name = "회원 알람 필터: 친구 요청 알림", example = "true")
        private Boolean notiFriendRequest;

        @ApiModelProperty(name = "회원 알람 필터: 친구 요청 수락 알림", example = "false")
        private Boolean notiFriendAccept;

        @ApiModelProperty(name = "회원 알람 필터: 챌린지 초대 알림", example = "true")
        private Boolean notiChallengeRequest;

        @ApiModelProperty(name = "회원 알람 필터: 챌린지 수락 알림", example = "false")
        private Boolean notiChallengeAccept;

        @ApiModelProperty(name = "회원 알람 필터: 챌린지 진행 알림", example = "true")
        private Boolean notiChallengeStart;

        @ApiModelProperty(name = "회원 알람 필터: 챌린지 취소 알림", example = "true")
        private Boolean notiChallengeCancel;

        @ApiModelProperty(name = "회원 알람 필터: 챌린지 결과 알림", example = "true")
        private Boolean notiChallengeResult;
    }
}
