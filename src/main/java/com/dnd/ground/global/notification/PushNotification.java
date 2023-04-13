package com.dnd.ground.global.notification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @description 푸시 알람 기록용 엔티티
 * @author  박찬호
 * @since   2023-03-20
 * @updated 1.컨벤션에 따른 수정
 *          - 2023-04-13 박찬호
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
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(name="target_nickname", nullable = false)
    private String nickname;
}
