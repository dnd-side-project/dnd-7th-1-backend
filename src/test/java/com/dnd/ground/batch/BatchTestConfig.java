package com.dnd.ground.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@ComponentScan(
        basePackages = { "com.dnd.ground" },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = { ".*_job$" }
                )
        }
)
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class BatchTestConfig {
}