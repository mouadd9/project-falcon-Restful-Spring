package org.falcon.progressionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.falcon.progressionservice.client")
public class ProgressionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgressionServiceApplication.class, args);
	}

}
