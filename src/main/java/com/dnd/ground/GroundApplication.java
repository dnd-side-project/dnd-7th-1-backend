package com.dnd.ground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GroundApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroundApplication.class, args);
	}

}
