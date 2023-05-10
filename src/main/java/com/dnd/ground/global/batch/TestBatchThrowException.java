package com.dnd.ground.global.batch;

import com.dnd.ground.global.log.CommonLogger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.batch.operations.BatchRuntimeException;

/**
 * @description 테스트를 위한 배치 정의
 *              여러 개의 Job이 존재하는 상황에서 Job이 제대로 주입이 안되었을 때, 해당 배치에서 예외를 던지도록 함.
 * @author  박찬호
 * @since   2023-04-28
 * @updated 1. 배치 생성
 *          2023-04-28 박찬호
 */


@Configuration
public class TestBatchThrowException {
    public TestBatchThrowException(JobBuilderFactory jobBuilderFactory,
                               StepBuilderFactory stepBuilderFactory,
                               JobParamDateTimeConverter wrong_job_param_converter,
                               @Qualifier("batchLogger") CommonLogger logger) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dateTimeConverter = wrong_job_param_converter;
        this.logger = logger;
    }
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobParamDateTimeConverter dateTimeConverter;
    private final CommonLogger logger;
    private static final String JOB_NAME = "wrong";

    @Bean(name = JOB_NAME + "_job")
    @Primary
    public Job challengeEndJob(Step challenge_end_step) {
        return jobBuilderFactory.get(JOB_NAME + "_job")
                .start(challenge_end_step)
                .build();
    }

    @JobScope
    @Bean(name = JOB_NAME + "_job_param_converter")
    public JobParamDateTimeConverter localDateTimeConverter(@Value("#{jobParameters[requestDate]}") String createdStr) {
        return new JobParamDateTimeConverter(createdStr);
    }

    @JobScope
    @Bean(name = JOB_NAME + "_step")
    public Step challengeEndStep(Tasklet throwExceptionTasklet) {
        return stepBuilderFactory.get(JOB_NAME + "_step")
                .tasklet(throwExceptionTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet throwExceptionTasklet() {
        logger.errorWrite(String.format("잘못된 배치가 실행되었습니다: %s", dateTimeConverter.getCreated().toString()));
        throw new BatchRuntimeException("잘못된 배치가 실행되었습니다.");
    }

}
