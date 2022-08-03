package com.dnd.ground.domain.challenge;

/**
 * @description 챌린지 상태
 *              Wait     - 수락 대기
 *              Progress - 진행
 *              Done     - 종료
 * @author  박찬호
 * @since   2022-07-26
 * @updated 1. Waiting → Wait 변경
 *          - 2022-08-01 박찬호
 */

public enum ChallengeStatus {
    Wait, Progress, Done
}
