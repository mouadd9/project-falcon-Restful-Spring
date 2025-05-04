package com.falcon.falcon.service;

import com.falcon.falcon.dto.UserDTO;
import com.falcon.falcon.exceptions.userExceptions.UserAlreadyExistsException;

// User management
public interface UserService {
    void validateEmailNotExists(String email) throws UserAlreadyExistsException;
    UserDTO createUser(UserDTO user) throws UserAlreadyExistsException;
}