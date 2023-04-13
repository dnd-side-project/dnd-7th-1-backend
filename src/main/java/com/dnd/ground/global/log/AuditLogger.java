package com.dnd.ground.global.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @description Audit Log 기록을 위한 Logger 클래스
 * @author 박찬호
 * @since 2023-03-08
 * @updated 1.Audit Log 기록을 위한 클래스 생성
 *          - 2023.03.08 박찬호
 */
@Component
@Qualifier("auditLogger")
public class AuditLogger implements CommonLogger {
    private final Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    @Override
    public void write(String log) {
        this.logger.info(log);
    }

    @Override
    public void errorWrite(String log) {
        this.logger.error(log);
    }
}
