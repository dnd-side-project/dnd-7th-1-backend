package com.dnd.ground.domain.challenge.dto;

import com.dnd.ground.domain.challenge.ChallengeColor;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 챌린지와 관련한 Response DTO
 * @author  박찬호
 * @since   2022-08-12
 * @updated 1. 챌린지 색깔 관련 수정
 *          - 2022.08.15 박찬호
 */


public class ChallengeResponseDto {

    /*상태에 상관 없이 사용되는 챌린지 관련 공통 정보*/
    @Data
    @AllArgsConstructor
    static public class CInfo {
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

}