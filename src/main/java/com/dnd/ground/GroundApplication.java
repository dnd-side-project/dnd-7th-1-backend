package com.dnd.ground;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication
public class GroundApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(GroundApplication.class).run(args);
	}
}