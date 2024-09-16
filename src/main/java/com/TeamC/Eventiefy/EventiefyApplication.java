package com.TeamC.Eventiefy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.TeamC.Eventiefy.entity", "com.TeamC.Eventiefy.user"})
@EnableJpaRepositories(basePackages = "com.TeamC.Eventiefy.repository")
@ComponentScan(basePackages = "com.TeamC.Eventiefy") // Ensure this matches your package structure
public class EventiefyApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventiefyApplication.class, args);
	}
}
