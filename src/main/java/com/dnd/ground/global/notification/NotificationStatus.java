package com.dnd.ground.global.notification;

/**
 * @description 푸시 알람 상태 클래스
 *              WAIT    : 대기
 *              RESERVED: 전송 예정
 *              SEND    : 전송 완료
 *              FAIL    : 전송 실패
 * @author  박찬호
 * @since   2023-03-22
 * @updated 1.저장 전 대기 상태 추가
 *          - 2023-05-05 박찬호
 */


public enum NotificationStatus {
    WAIT,
    RESERVED,
    SEND,
    FAIL
}
