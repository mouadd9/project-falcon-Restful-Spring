package com.falcon.falcon.service.interfaces;

import com.falcon.falcon.DTOs.SignUpRequest;
import com.falcon.falcon.DTOs.SignUpResponse;
import com.falcon.falcon.DTOs.VerificationCodeRequest;
import com.falcon.falcon.DTOs.VerificationCodeResponse;
import com.falcon.falcon.exception.UserAlreadyExistsException;

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
    SignUpResponse completeRegistration(SignUpRequest request);


}
