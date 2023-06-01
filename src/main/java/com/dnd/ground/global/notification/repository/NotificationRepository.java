package com.dnd.ground.global.notification.repository;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.notification.PushNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @description 푸시알람 레포지토리
 * @author  박찬호
 * @since   2023-03-21
 * @updated 1.알람함 목록 조회 쿼리 수정
 *          - 2023-05-31 박찬호
 */


public interface NotificationRepository extends JpaRepository<PushNotification, String> {
    @Query(value = "SELECT p " +
            "FROM PushNotification p " +
            "JOIN FETCH PushNotificationParam np " +
            "ON np.notification = p " +
            "INNER JOIN User u " +
            "ON p.user = u " +
            "WHERE u.nickname = :nickname " +
                "AND p.status <> 'FAIL' " +
                "AND p.isDeleted <> true " +
            "ORDER BY p.reserved DESC")
    List<PushNotification> findNotifications(@Param("nickname") String nickname, Pageable pageable);

    @Query("SELECT p FROM PushNotification p WHERE p.user = :user")
    Optional<PushNotification> findByUser(@Param("user") User user);
}
