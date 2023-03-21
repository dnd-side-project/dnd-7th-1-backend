package com.dnd.ground.global.notification;

/**
 * @description 푸시 알람 상태 클래스
 *              RESERVED: 전송 예정
 *              SEND    : 전송 완료
 *              FAIL    : 전송 실패
 * @author  박찬호
 * @since   2023-03-22
 * @updated 1.전송 예정, 완료, 실패 상태 생성
 *          - 2023-03-2 박찬호
 */


public enum NotificationStatus {
    RESERVED,
    SEND,
    FAIL
}
