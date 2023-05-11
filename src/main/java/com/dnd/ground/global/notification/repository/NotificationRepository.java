package com.dnd.ground.global.notification.repository;

import com.dnd.ground.global.notification.PushNotification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @description 푸시알람 레포지토리
 * @author  박찬호
 * @since   2023-03-21
 * @updated 1.푸시 알람 레포지토리 생성
 *          - 2023-03-21 박찬호
 */


public interface NotificationRepository extends JpaRepository<PushNotification, String> {
}
