package com.falcon.falcon.controllers;

import com.falcon.falcon.dtos.auth.LoginRequest;
import com.falcon.falcon.dtos.auth.SignUpRequest;
import com.falcon.falcon.dtos.auth.VerificationCodeRequest;
import com.falcon.falcon.dtos.auth.VerificationCodeResponse;
import com.falcon.falcon.facades.AuthenticationFacade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController { // The controller is now much simpler, focusing only on HTTP concerns
    private final AuthenticationFacade authFacade;

    public AuthController(AuthenticationFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/verification-codes")
    public ResponseEntity<VerificationCodeResponse> sendVerificationCode(@RequestBody VerificationCodeRequest verificationCodeRequest) {
        VerificationCodeResponse verificationCodeResponse = this.authFacade.initiateRegistration(verificationCodeRequest);
        return new ResponseEntity<>(verificationCodeResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignUpRequest signUpRequest) {
        // Complete registration
        authFacade.completeRegistration(signUpRequest);

        // Authenticate the new user to generate token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(signUpRequest.getUsername());
        loginRequest.setPassword(signUpRequest.getPassword());

        Map<String, String> token = authFacade.authenticate(loginRequest);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest credentials) {
        Map<String, String> token = authFacade.authenticate(credentials);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
