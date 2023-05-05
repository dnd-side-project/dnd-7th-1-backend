package com.dnd.ground.global.notification;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.UserProperty;
import com.dnd.ground.global.log.CommonLogger;
import com.dnd.ground.global.log.NotificationLogger;
import com.dnd.ground.global.util.ApplicationContextProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @description 알람 이벤트 발생을 위한 포맷 클래스
 * @author  박찬호
 * @since   2023-03-17
 * @updated 1.생성자 추가 및 Builder 적용
 *          2023-04-10 박찬호
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
        findUserPropertyInPersistenceContext(users);
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
    }

    public NotificationForm(List<User> users,
                            List<String> titleParams,
                            List<String> contentParams,
                            NotificationMessage message,
                            Map<String, String> data) {
        findUserPropertyInPersistenceContext(users);
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
        this.data = data;
    }

    public NotificationForm(List<User> users,
                            List<String> titleParams,
                            List<String> contentParams,
                            NotificationMessage message,
                            Map<String, String> data,
                            LocalDateTime reserved) {
        findUserPropertyInPersistenceContext(users);
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
        this.data = data;
        this.reserved = reserved;
    }

    public NotificationForm(List<User> users, NotificationMessage message, LocalDateTime reserved) {
        findUserPropertyInPersistenceContext(users);
        this.users = users;
        this.message = message;
        this.created = LocalDateTime.now();
        this.reserved = reserved;
    }

    @PostConstruct
    public void init() {
        this.logger = (NotificationLogger) ApplicationContextProvider.getBean(NotificationLogger.class);


    }

    private void findUserPropertyInPersistenceContext(List<User> users) {
        for (User user : users) {
            try {
                UserProperty property = user.getProperty();
                if (property.getFcmToken() == null)
                    Hibernate.initialize(property);
                user.setUserProperty(property);
            } catch (HibernateException e) {
                /**
                 * 영속성 컨텍스트에서 UserProperty를 조회해서 세팅.
                 * UserProperty가 세팅되지 않으면 NotificationService에서 걸러냄.
                 */
                this.logger.errorWrite("User Property를 찾을 수 없습니다.");
            }
        }
    }
}
