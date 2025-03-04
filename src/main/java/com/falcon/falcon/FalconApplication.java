package com.falcon.falcon;

import com.falcon.falcon.model.Role;
import com.falcon.falcon.model.User;
import com.falcon.falcon.repository.RoleRepository;
import com.falcon.falcon.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FalconApplication implements CommandLineRunner {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public FalconApplication(UserRepository userRepository, RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	public static void main(String[] args) {

		SpringApplication.run(FalconApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Falcon application started");
		/*User user = new User();
		user.setUsername("Falcon");
		user.setPassword("Falcon");
		user.setEmail("falcon@falcon.com");
		Role role = new Role();
		role.setName("ROLE_USER");
		roleRepository.save(role);
		user.getRoles().add(role);
		userRepository.save(user);*/
	}
}
