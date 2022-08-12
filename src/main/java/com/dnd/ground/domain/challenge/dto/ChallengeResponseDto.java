package com.dnd.ground.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 챌린지와 관련한 Response DTO
 * @author  박찬호
 * @since   2022-08-12
 * @updated 1. 진행 대기 상태의 챌린지 조회 기능 구현
 *          - 2022.08.12 박찬호
 */


public class ChallengeResponseDto {

    /*상태에 상관 없이 사용되는 챌린지 관련 공통 정보*/
    @Data
    @AllArgsConstructor
    static public class CInfo {
        private String name;
        private LocalDate started;
    }

    /*진행 대기 중 상태의 챌린지 정보*/
    @Data
    @AllArgsConstructor
    @Builder
    static public class Wait {
        private String name;
        private LocalDate started;
        private LocalDate ended;
        private Integer totalCount;
        private Integer readyCount;
    }

}