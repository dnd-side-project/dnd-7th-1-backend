package com.dnd.ground.global.log;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @description 배치 작업 중 발생하는 로그를 기록하기 위한 Logger
 * @author 박찬호
 * @since 2023-04-21
 * @updated 1.로거 생성
 *          - 2023.04.21 박찬호
 */

@Component
@Qualifier("batchLogger")
public class BatchLogger extends CommonLoggerAbs {
    public BatchLogger() {
        super(BatchLogger.class);
    }
}
