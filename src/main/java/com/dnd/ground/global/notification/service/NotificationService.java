package com.dnd.ground.global.notification.service;

import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.notification.dto.NotificationResponseDto;

import java.util.List;

/**
 * @description 푸시 알람 관련 서비스 인터페이스
 * @author  박찬호
 * @since   2023-05-13
 * @updated  1.알람함 비우기 API 구현
 *          - 2023-05-24 박찬호
 */

public interface NotificationService {
    List<NotificationResponseDto> getNotifications(String nickname);
    ExceptionCodeSet readNotification(String messageId);
    ExceptionCodeSet deleteNotification(List<String> messageIds);
}
