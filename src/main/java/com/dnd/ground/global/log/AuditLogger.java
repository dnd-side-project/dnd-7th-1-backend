package com.dnd.ground.global.log;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * @description Audit Log 기록을 위한 Logger 클래스
 * @author 박찬호
 * @since 2023-03-08
 * @updated 1.추상 클래스를 활용한 중복 코드 최소화
 *          - 2023.03.21 박찬호
 */
@Component
@RequestScope
@Qualifier("auditLogger")
public class AuditLogger extends CommonLoggerAbs {
    public AuditLogger() {
        super(AuditLogger.class);
    }
}
