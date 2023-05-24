package com.dnd.ground.global.notification.service;

import com.dnd.ground.global.exception.CommonException;
import com.dnd.ground.global.exception.ExceptionCodeSet;
import com.dnd.ground.global.notification.NotificationStatus;
import com.dnd.ground.global.notification.PushNotification;
import com.dnd.ground.global.notification.dto.NotificationResponseDto;
import com.dnd.ground.global.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @description 푸시 알람 관련 서비스 클래스
 * @author  박찬호
 * @since   2023-05-13
 * @updated  1.알람함 비우기 API 구현
 *          - 2023-05-24 박찬호
 */

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponseDto> getNotifications(String nickname) {
        return notificationRepository.findNotifications(nickname, PageRequest.of(0, 20));
    }

    /*푸시 알람 읽기 (요청이 들어오면 항상 true)*/
    @Override
    @Transactional
    public ExceptionCodeSet readNotification(String messageId) {
        Optional<PushNotification> notificationOpt = notificationRepository.findById(messageId);

        if (notificationOpt.isPresent()) {
            PushNotification notification = notificationOpt.get();
            notification.read();

            if (notification.getStatus() != NotificationStatus.SEND) notification.updateStatus(NotificationStatus.SEND);

            return ExceptionCodeSet.OK;
        } else return ExceptionCodeSet.NOT_FOUND_NOTIFICATION;
    }

    @Override
    @Transactional
    public ExceptionCodeSet deleteNotification(List<String> messageIds) {
        List<PushNotification> notifications = notificationRepository.findAllById(messageIds);
        for (PushNotification notification : notifications) {
            notification.delete();
            messageIds.remove(notification.getMessageId());
        }

        if (messageIds.isEmpty()) return ExceptionCodeSet.OK;
        else throw new CommonException(ExceptionCodeSet.NOTIFICATION_DELETE_FAILED);
    }
}
