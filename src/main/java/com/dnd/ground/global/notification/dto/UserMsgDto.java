package com.dnd.ground.global.notification.dto;

import com.dnd.ground.global.notification.PushNotification;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 회원-푸시알람 메시지 DTO
 * @author  박찬호
 * @since   2023-03-20
 * @updated 1.PushNotification 추가
 *          - 2023-05-08 박찬호
 */

@Getter
@AllArgsConstructor
public class UserMsgDto {
    private String nickname;
    private Message message;
    private PushNotification notification;
}
