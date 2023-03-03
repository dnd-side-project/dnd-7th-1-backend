package com.dnd.ground.domain.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 챌린지 색깔(메인 화면에서 영역 색깔과 마커 색깔)
 *              PINK     - 나와 챌린지 1개 하는 유저
 *              YELLOW   - 나와 챌린지 2개 하는 유저
 *              RED      - 나와 챌린지 3개 하는 유저
 *              GREEN    - 내 영역
 * @author  박찬호
 * @since   2022-08-16
 * @updated 1. 코드 컨벤션 변경에 따른 수정(ENUM: 대문자 및 스네이크 케이스)
 *          2. 내 영역 표시용 GREEN 추가
 *          - 2023-03-03 박찬호
 */

@Getter
@AllArgsConstructor
public enum ChallengeColor {
    PINK("PINK"),
    YELLOW("YELLOW"),
    RED("RED"),
    GREEN("GREEN");

    private final String value;
}