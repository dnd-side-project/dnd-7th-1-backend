package com.dnd.ground.global.log;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @description 푸시 알람 Logging을 위한 Logger 클래스
 * @author 박찬호
 * @since 2023-03-09
 * @updated 1.추상 클래스를 활용한 중복 코드 최소화
 *          - 2023.03.21 박찬호
 */

@Component
@Qualifier("notificationLogger")
public class NotificationLogger extends CommonLoggerAbs {
    public NotificationLogger() {
        super(NotificationLogger.class);
    }
}
