package com.dnd.ground.domain.challenge;

/**
 * @description 챌린지 상태(Challenge, UserChallenge 둘 다 사용)
 *              WAIT               - 대기 상태
 *              MASTER             - 주최자 대기 상태(UC Only)
 *              MASTER_PROGRESS    - 주최자 진행 중인 상태(UC Only)
 *              PROGRESS           - 진행 중
 *              DONE               - 종료
 *              MASTER_DONE        - 주최자 종료 상태(UC Only)
 *              REJECT             - 거절(UC Only)
 * @author  박찬호
 * @since   2022-07-26
 * @updated 1. 코드 컨벤션 변경에 따른 수정(ENUM: 대문자 및 스네이크 케이스)
 *          2. 주최자 상태 완전 분리
 *          - 2023-03-03 박찬호
 */

public enum ChallengeStatus {
    WAIT, MASTER,
    PROGRESS, MASTER_PROGRESS,
    DONE, MASTER_DONE,
    REJECT
}
