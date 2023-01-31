package com.dnd.ground;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GroundApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(GroundApplication.class).run(args);
	}
}