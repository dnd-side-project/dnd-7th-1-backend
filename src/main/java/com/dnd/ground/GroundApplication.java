package com.dnd.ground;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GroundApplication {

	public static final String APPLICATION_LOCATIONS =
			"spring.config.location="
					+ "classpath:application.yml,"
					+ "classpath:application-dev.properties,"
//               + "classpath:application-production.properties,"
					+ "classpath:aws.yml";

	public static void main(String[] args) {

		new SpringApplicationBuilder(GroundApplication.class)
				.properties(APPLICATION_LOCATIONS)
				.run(args);
	}
}