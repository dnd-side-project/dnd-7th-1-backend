package com.dnd.ground.global.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @description 코드 중복을 최소화하기 위한 CommonLogger 추상 클래스
 * @author 박찬호
 * @since 2023-03-21
 * @updated 1.info, error 레벨의 로깅을 위한 메소드 구현
 *          - 2023.03.21 박찬호
 */
public abstract class CommonLoggerAbs implements CommonLogger {
    private final Logger logger;
    protected CommonLoggerAbs(Class<? extends CommonLogger> target) {
        this.logger = LoggerFactory.getLogger(target);
    }

    @Override
    public void write(String log) {
        this.logger.info(log);
    }

    @Override
    public void errorWrite(String log) {
        this.logger.error(log);
    }
}
