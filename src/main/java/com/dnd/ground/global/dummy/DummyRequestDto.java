package com.dnd.ground.global.dummy;

import com.dnd.ground.domain.challenge.ChallengeStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @description 더미 데이터 생성을 위한 Request DTO
 * @author  박찬호
 * @since   2022-10-04
 * @updated 1.챌린지 uuid 조회
 *          2.챌린지 상태 변경
 *          3.UC 상태 변경
 *          - 2022.11.23 박찬호
 */
public class DummyRequestDto {

    /*회원 생성용 DTO*/
    @Data
    public static class DummyUser {
        @ApiModelProperty(name = "닉네임(중복X)", example = "NickA", required = true)
        private String nickname;
        @ApiModelProperty(name = "이메일(중복X)", example = "dummy@gmail.com", required = true)
        private String mail;
        @ApiModelProperty(name = "소개 메시지", example = "프로필에 출력될 소개 메시지.")
        private String intro;
    }

    /*운동 기록 생성용 DTO*/
    @Data
    public static class DummyRecord {

        @ApiModelProperty(name = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(name = "운동 시작 시간", example = "2022-08-23 22:00:00")
        private LocalDateTime started;

        @ApiModelProperty(name = "운동 끝나는 시간", example = "2022-08-23 22:30:00")
        private LocalDateTime ended;

        @ApiModelProperty(name = "운동 시간", example = "928")
        private Integer exerciseTime;

        @ApiModelProperty(name = "거리", example = "123")
        private Integer distance;

        @ApiModelProperty(name = "걸음 수", example = "564")
        private Integer stepCount;

        @ApiModelProperty(name = "메시지", example = "운동 기록에 남기는 메시지")
        private String message;

        @ApiModelProperty(name = "기록될 영역 정보 | 포맷: [ {위도:경도}, {위도:경도} ]",
                example = "[[37.123123, 127.123123], [37.234234, 127.234234]]", dataType = "list", required = true)
        ArrayList<ArrayList<Double>> matrices;
    }

    /*운동 기록에 영역을 추가하기 위한 DTO*/
    @Data
    public static class DummyRecordMatrix {
        @ApiModelProperty(name = "영역을 추가할 운동 기록 번호", example = "4")
        private Long recordId;

        @ApiModelProperty(name = "기록될 영역 정보 | 포맷: [ {위도:경도}, {위도:경도} ]",
                example = "[[37.123123, 127.123123], [37.234234, 127.234234]]", dataType = "list", required = true)
        private ArrayList<ArrayList<Double>> matrices;
    }

    /*챌린지 상태를 변경하기 위한 DTO*/
    @Data
    public static class DummyChallengeStatus {
        @ApiModelProperty(name = "챌린지 UUID", example = "11ed6b2bf98211da9cae0b652cf586a6")
        private String uuid;
        @ApiModelProperty(name = "바꾸고자 하는 챌린지 상태", example = "Wait")
        private ChallengeStatus status;
    }

    /*챌린지에 참여하는 회원의 상태를 변경하기 위한 DTO*/
    @Data
    public static class DummyUCStatus {
        @ApiModelProperty(name = "닉네임", example = "NickA", required = true)
        private String nickname;

        @ApiModelProperty(name = "챌린지 UUID", example = "11ed6b2bf98211da9cae0b652cf586a6")
        private String uuid;

        @ApiModelProperty(name = "바꾸고자 하는 챌린지 상태", example = "Wait")
        private ChallengeStatus status;
    }
}
