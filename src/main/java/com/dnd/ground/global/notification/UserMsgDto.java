package com.dnd.ground.global.notification;

import com.dnd.ground.domain.user.User;
import com.google.firebase.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description 회원-푸시알람 메시지 DTO
 * @author  박찬호
 * @since   2023-03-20
 * @updated 1.DTO 생성
 *          - 2023-03-21 박찬호
 */

@Getter
@AllArgsConstructor
public class UserMsgDto {
    private User user;
    private Message message;
}
