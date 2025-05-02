package com.falcon.falcon.service;

import com.falcon.falcon.dto.authDto.SignUpRequest;
import com.falcon.falcon.dto.UserDTO;
import com.falcon.falcon.dto.authDto.VerificationCodeRequest;
import com.falcon.falcon.dto.authDto.VerificationCodeResponse;
import com.falcon.falcon.exceptions.userExceptions.UserAlreadyExistsException;
import org.springframework.security.core.Authentication;

import java.util.Map;

// the main entry point
// responsible for orchestrating the flows between more specialized services.
// the AuthController will only inject the AuthService
// it will only call methods in AuthService
// then AuthService will orchestrate calls to specialized services
public interface AuthService {
    // AuthService will have methods that delegate work to other services
      // requestVerificationCode ----(Check email)----> UserService
      // requestVerificationCode ----(generate verification request)----> VerificationService
      // requestVerificationCode ----(store verification request in Redis)----> VerificationService
    VerificationCodeResponse requestVerificationCode(VerificationCodeRequest request) throws UserAlreadyExistsException;
      // completeRegistration ----(Validate code)----> VerificationService
      // completeRegistration ----(create user)----> UserService
      // completeRegistration ----(generate jwt)----> jwt service
    UserDTO completeRegistration(SignUpRequest request);

    Map<String, String> generateAccessToken(Authentication result);
}
