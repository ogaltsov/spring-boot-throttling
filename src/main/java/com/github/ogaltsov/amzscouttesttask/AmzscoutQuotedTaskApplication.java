package com.github.ogaltsov.amzscouttesttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmzscoutQuotedTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmzscoutQuotedTaskApplication.class, args);
	}

}
