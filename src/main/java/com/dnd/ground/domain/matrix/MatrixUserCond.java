package com.dnd.ground.domain.matrix;


/**
 * @description 영역을 조회할 때 대상 인원을 필터링하기 위한 ENUM
 *              ALL       : 본인, 친구, 챌린지 회원을 포함한 전체 인원
 *              CHALLENGE : 본인을 포함한 챌린지 인원
 * @author  박찬호
 * @since   2023.03.12
 * @updated 1. ENUM 생성
 *          - 2023.03.12 박찬호
 */


public enum MatrixUserCond {
    ALL,
    CHALLENGE
}
