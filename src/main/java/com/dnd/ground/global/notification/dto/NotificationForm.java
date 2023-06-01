package com.dnd.ground.global.notification.dto;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.global.log.CommonLogger;
import com.dnd.ground.global.log.NotificationLogger;
import com.dnd.ground.global.notification.NotificationMessage;
import com.dnd.ground.global.util.ApplicationContextProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description 알람 이벤트 발생을 위한 포맷 클래스
 * @author  박찬호
 * @since   2023-03-17
 * @updated 1.토큰 관리 방식 변경에 따른 메시지 구성 방식 수정
 *          2023-05-11 박찬호
 */

@Getter
@AllArgsConstructor
@Builder
public class NotificationForm {
    private LocalDateTime created;
    private LocalDateTime reserved;
    private List<User> users;
    private List<String> titleParams;
    private List<String> contentParams;
    private Map<String, String> data;
    private NotificationMessage message;
    private CommonLogger logger;

    public NotificationForm(List<User> users,
                            List<String> titleParams,
                            List<String> contentParams,
                            NotificationMessage message) {
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
        init();
    }

    public NotificationForm(List<User> users,
                            List<String> titleParams,
                            List<String> contentParams,
                            NotificationMessage message,
                            Map<String, String> data) {
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
        this.data = data;
        init();
    }

    public NotificationForm(List<User> users,
                            List<String> titleParams,
                            List<String> contentParams,
                            NotificationMessage message,
                            Map<String, String> data,
                            LocalDateTime reserved) {
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
        this.data = data;
        this.reserved = reserved;
        init();
    }

    public NotificationForm(List<User> users, NotificationMessage message, LocalDateTime reserved) {
        this.users = users;
        this.message = message;
        this.created = LocalDateTime.now();
        this.reserved = reserved;
        init();
    }

    public void init() {
        this.logger = (NotificationLogger) ApplicationContextProvider.getBean(NotificationLogger.class);

        if (this.message == null) {
            this.logger.errorWrite("메시지가 존재하지 않습니다.");
            this.users.clear(); //메시지가 존재하지 않으면, 회원을 모두 삭제해서 전송하지 않도록 함.
            return;
        }

        //필터에 따라 유저 제외시키기
        List<User> exceptUsers = new ArrayList<>();
        for (User user : this.users) {
            if (!user.getProperty().checkNotiFilter(this.message)) exceptUsers.add(user);
        }
        this.users.removeAll(exceptUsers);
    }
}
