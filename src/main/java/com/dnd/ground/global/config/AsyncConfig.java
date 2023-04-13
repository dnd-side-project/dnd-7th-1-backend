package com.dnd.ground.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @description 비동기 설정 클래스
 * @author  박찬호
 * @since   2023-03-17
 * @updated 1.푸시 알람 비동기 처리에 따른 스레드 설정
 *          - 2023-03-17 박찬호
 */

@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    @Bean("notification")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        final String THREAD_NAME_PREFIX = "NOTIFICATION-";

        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors*2);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }
}
