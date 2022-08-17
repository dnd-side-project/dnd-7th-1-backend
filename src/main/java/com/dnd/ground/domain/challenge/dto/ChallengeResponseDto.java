package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.ChallengeColor;
import com.dnd.ground.domain.challenge.ChallengeType;
import com.dnd.ground.domain.matrix.dto.MatrixDto;
import com.dnd.ground.domain.user.dto.UserResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description 챌린지와 관련한 Response DTO
 * @author  박찬호
 * @since   2022-08-12
 * @updated 1. 챌린지 상세 조회 기능 구현을 위한 Detail 메소드 생성
 *          - 2022.08.17 박찬호
 */


public class ChallengeResponseDto {

    /*상태에 상관 없이 사용되는 챌린지 관련 공통 정보*/
    @Data
    @AllArgsConstructor
    static public class CInfoRes {
        @ApiModelProperty(value="챌린지 이름", example="챌린지A")
        private String name;

        @ApiModelProperty(value="챌린지 이름", example="챌린지A")
        private LocalDate started;
    }

    /*진행 대기 중 상태의 챌린지 정보*/
    @Data
    @AllArgsConstructor
    @Builder
    static public class Wait {
        @ApiModelProperty(value="챌린지 이름", example="챌린지A")
        private String name;

        @ApiModelProperty(value="챌린지 시작 날짜", example="2022-08-12")
        private LocalDate started;

        @ApiModelProperty(value="챌린지 종료 날짜(시작 날짜 주의 일요일)", example="2022-08-14")
        private LocalDate ended;

        @ApiModelProperty(value="챌린지에 참여하는 전체 인원", example="4")
        private Integer totalCount;

        @ApiModelProperty(value="챌린지를 수락한 인원(주최자 포함)", example="2")
        private Integer readyCount;

        @ApiModelProperty(value="챌린지 색깔", example="Red")
        private ChallengeColor color;
    }

    /*진행 중 상태의 챌린지 정보*/
    @Data
    @Builder
    static public class Progress {
        @ApiModelProperty(value="챌린지 이름", example="챌린지A")
        private String name;

        @ApiModelProperty(value="챌린지 시작 날짜", example="2022-08-15")
        private LocalDate started;

        @ApiModelProperty(value="챌린지 종료 날짜(시작 날짜 주의 일요일)", example="2022-08-15")
        private LocalDate ended;

        @ApiModelProperty(value="챌린지 내 랭킹(영역)", example="2")
        private Integer rank;

        @ApiModelProperty(value="챌린지 색깔", example="Red")
        private ChallengeColor color;
    }

    /*진행 완료 상태의 챌린지 정보*/
    @Data
    @Builder
    static public class Done {
        @ApiModelProperty(value="챌린지 이름", example="챌린지A")
        private String name;

        @ApiModelProperty(value="챌린지 시작 날짜", example="2022-08-15")
        private LocalDate started;

        @ApiModelProperty(value="챌린지 종료 날짜(시작 날짜 주의 일요일)", example="2022-08-15")
        private LocalDate ended;

        @ApiModelProperty(value="챌린지 내 랭킹(영역)", example="2")
        private Integer rank;

        @ApiModelProperty(value="챌린지 색깔", example="Red")
        private ChallengeColor color;
    }

    /*초대 받은 챌린지 정보*/
    @Data
    @Builder
    static public class Invite {
        @ApiModelProperty(value="챌린지 이름", example="챌린지A")
        private String name;

        @ApiModelProperty(value="주최자 닉네임(초대자)", example="NickA")
        private String InviterNickname;

        @ApiModelProperty(value="초대 메시지(Nullable)", example="초대 메시지입니다.")
        private String message;

        @ApiModelProperty(value="초대 시간(yyyy-MM-dd hh:mm)", example="2022-08-12 22:10")
        private String created;
    }

    @Data
    @Builder
    static public class Detail {
        @ApiModelProperty(value="회원 닉네임", example="NickA", dataType = "String", notes="회원 닉네임")
        private String name;

        @ApiModelProperty(value="챌린지 종류", example="Widen", dataType = "String", notes="일주일 챌린지 종류")
        private ChallengeType type;

        @ApiModelProperty(value="챌린지 색상", example="Pink", dataType = "String", notes="회원-챌린지 색상")
        private ChallengeColor color;

        @ApiModelProperty(value="챌린지 시작 날짜", example="2022-08-17", dataType = "LocalDate", notes="챌린지 시작 날짜")
        private LocalDate started;

        @ApiModelProperty(value="챌린지 종료 날짜", example="2022-08-21", dataType = "LocalDate", notes="챌린지 종료 날짜(일요일)")
        private LocalDate ended;

        @ApiModelProperty(value="영역 정보", example="[{\"latitude\": 1.0,\"longitude\": 1.0}]", dataType = "Array[double, double]", notes="영역 정보 배열")
        private List<MatrixDto> matrices;

        @ApiModelProperty(value="랭킹 정보", example="[{\"rank\": 1, \"nickname\": \"NickB\", \"score\": 4}]", dataType = "Array[int, String, int]", notes="랭킹 정보")
        private List<UserResponseDto.Ranking> rankings;

        //내 기록
        @ApiModelProperty(value="거리", example="100", dataType = "int", notes="일주일 동안 기록들의 거리 합")
        private Integer distance;

        @ApiModelProperty(value="운동 시간", example="15", dataType = "int", notes="일주일 동안 기록 시간 합 (단위: 초)")
        private Integer exerciseTime;

        @ApiModelProperty(value="걸음 수", example="15", dataType = "int", notes="일주일 동안 기록의 걸음 수 합")
        private Integer stepCount;
    }

}