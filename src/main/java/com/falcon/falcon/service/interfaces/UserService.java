package com.falcon.falcon.service.interfaces;

import com.falcon.falcon.exception.UserAlreadyExistsException;

public interface UserService {
    void validateEmailNotExists(String email) throws UserAlreadyExistsException;
}
