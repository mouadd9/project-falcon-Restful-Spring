package com.falcon.falcon.service.interfaces;

import com.falcon.falcon.DTOs.SignUpRequest;
import com.falcon.falcon.DTOs.UserDTO;
import com.falcon.falcon.exceptions.UserAlreadyExistsException;

public interface UserService {
    void validateEmailNotExists(String email) throws UserAlreadyExistsException;
    UserDTO createUser(UserDTO user) throws UserAlreadyExistsException;
}
