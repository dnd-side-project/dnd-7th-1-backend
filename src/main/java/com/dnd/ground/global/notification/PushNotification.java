package com.dnd.ground.global.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @description 푸시 알람 기록용 엔티티
 * @author  박찬호
 * @since   2023-03-20
 * @updated 1.푸시 알람 비동기 처리에 따른 스레드 설정
 *          - 2023-03-21 박찬호
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushNotification {
    @Id
    private String messageId;
    @Column(name="title", nullable = false)
    private String title;
    @Column(name="content", nullable = false)
    private String content;
    @Column(name="created", nullable = false)
    private LocalDateTime created;
    @Column(name="reserved_time", nullable = false)
    private LocalDateTime reservedTime;
    @Column(name="status", nullable = false)
    private NotificationStatus status;
    @Column(name="target_nickname", nullable = false)
    private String nickname;
}
