package com.dnd.ground.global.notification.repository;

import com.dnd.ground.global.notification.PushNotification;
import com.dnd.ground.global.notification.dto.NotificationResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @description 푸시알람 레포지토리
 * @author  박찬호
 * @since   2023-03-21
 * @updated 1.연관관계 매핑에 따른 쿼리 수정
 *          - 2023-05-26 박찬호
 */


public interface NotificationRepository extends JpaRepository<PushNotification, String> {

    @Query(value = "SELECT new com.dnd.ground.global.notification.dto.NotificationResponseDto(p.messageId, p.title, p.content, p.isRead, p.type, p.reserved) " +
            "FROM PushNotification p " +
            "WHERE p.user.nickname = :nickname " +
                "AND p.status <> 'FAIL' " +
                "AND p.isDeleted <> true " +
            "ORDER BY p.reserved DESC")
    List<NotificationResponseDto> findNotifications(@Param("nickname") String nickname, Pageable pageable);
}
