package com.falcon.falcon.services;

import com.falcon.falcon.dtos.UserDTO;
import com.falcon.falcon.exceptions.userExceptions.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// User management
public interface UserService {
    void validateEmailNotExists(String email) throws UserAlreadyExistsException;
    UserDTO createUser(UserDTO user) throws UserAlreadyExistsException;
    UserDTO getUserByUsername(String username) throws UsernameNotFoundException;
}