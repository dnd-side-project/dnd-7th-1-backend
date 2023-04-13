package com.dnd.ground.global.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @description 푸시 알람 Logging을 위한 Logger 클래스
 * @author 박찬호
 * @since 2023-03-09
 * @updated 1.푸시알람 Logging을 위한 클래스 생성
 *          - 2023.03.09 박찬호
 */
@Component
@Qualifier("notificationLogger")
public class NotificationLogger implements CommonLogger {
    private final Logger logger = LoggerFactory.getLogger(NotificationLogger.class);

    @Override
    public void write(String log) {
        this.logger.info(log);
    }

    @Override
    public void errorWrite(String log) {
        this.logger.error(log);
    }
}
