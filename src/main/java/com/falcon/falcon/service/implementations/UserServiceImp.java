package com.falcon.falcon.service.implementations;

import com.falcon.falcon.exception.UserAlreadyExistsException;
import com.falcon.falcon.repository.UserRepository;
import com.falcon.falcon.service.interfaces.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    private UserRepository userRepository;

    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmailNotExists(String email) throws UserAlreadyExistsException {
        this.userRepository.findByEmail(email).ifPresent(u -> {
            throw new UserAlreadyExistsException("User with email " + u.getEmail() + " already exists");
        });
    }

}
