package com.dnd.ground.global.notification;

import com.dnd.ground.domain.user.User;
import com.dnd.ground.domain.user.UserProperty;
import com.dnd.ground.global.log.CommonLogger;
import com.dnd.ground.global.log.NotificationLogger;
import com.dnd.ground.global.util.ApplicationContextProvider;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description 알람 이벤트 발생을 위한 포맷 클래스
 * @author  박찬호
 * @since   2023-03-17
 * @updated 1.데이터를 전달하기 위한 필드 생성
 *          2.메시지 속 파라미터를 파싱하기 위한 메소드 생성
 *          3.알람 전송의 비동기 처리로 인해, 영속성 컨텍스트를 닫기 전 UserProperty를 꺼내는 메소드 생성
 *          2023-04-10 박찬호
 */

@Getter
public class NotificationForm {
    private List<User> users;
    private List<String> titleParams;
    private List<String> contentParams;
    private Map<String, String> data;
    private NotificationMessage message;
    private CommonLogger logger;

    public NotificationForm(List<User> users, List<String> titleParams, List<String> contentParams, NotificationMessage message) {
        findUserPropertyInPersistenceContext(users);
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
    }

    public NotificationForm(List<User> users, List<String> titleParams, List<String> contentParams, NotificationMessage message, Map<String, String> data) {
        findUserPropertyInPersistenceContext(users);
        this.users = users;
        this.titleParams = titleParams;
        this.contentParams = contentParams;
        this.message = message;
        this.data = data;
    }

    @PostConstruct
    public void init() {
        this.logger = (NotificationLogger) ApplicationContextProvider.getBean(NotificationLogger.class);
    }

    private void findUserPropertyInPersistenceContext(List<User> users) {
        for (User user : new ArrayList<>(users)) {
            try {
                UserProperty property = user.getProperty();
                if (property.getId() == null)
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
