package com.dnd.ground.global.batch;

import com.dnd.ground.global.log.CommonLogger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @description 배치 Job 전/후 로그를 기록하기 위한 Listener
 * @author  박찬호
 * @since   2023-05-02
 * @updated 1. 클래스 생성
 *          - 2023.05.02 박찬호
 */

@Component
public class JobLoggerListener implements JobExecutionListener {
    public JobLoggerListener(@Qualifier("batchLogger") CommonLogger logger) {
        this.logger = logger;
    }
    private final CommonLogger logger;
    private static final String BEFORE_MSG = "%s 배치가 시작되었습니다. Job Params:%s";
    private static final String AFTER_MSG = "%s 배치가 종료되었습니다. Job Status:%s";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.write(String.format(BEFORE_MSG,
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobParameters())
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.write(String.format(AFTER_MSG,
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus()));
    }
}
