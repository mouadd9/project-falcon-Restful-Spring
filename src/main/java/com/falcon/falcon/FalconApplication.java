package com.falcon.falcon;

import com.falcon.falcon.model.Role;
import com.falcon.falcon.model.User;
import com.falcon.falcon.repository.RoleRepository;
import com.falcon.falcon.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FalconApplication implements CommandLineRunner {

	public FalconApplication() {}

	public static void main(String[] args) {
		System.out.println("Falcon application started");
		System.out.println("Loading env variables");
		Dotenv dotenv = Dotenv.configure().load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		// Start Spring Boot application
		SpringApplication.run(FalconApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Load .env file

	}
}
