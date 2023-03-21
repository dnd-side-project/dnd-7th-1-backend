package com.dnd.ground.global.notification;

import com.dnd.ground.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @description 알람 이벤트 발생을 위한 포맷 클래스
 * @author  박찬호
 * @since   2023-03-17
 * @updated 1.알람 대상 회원 리스트, 제목과 내용에 파싱될 파라미터, 메시지로 포맷 구성
 *          - 2023-03-17 박찬호
 */

@Getter
@AllArgsConstructor
public class NotificationForm {
    private List<User> users;
    private List<String> titleParams;
    private List<String> contentParams;
    private NotificationMessage message;
}
