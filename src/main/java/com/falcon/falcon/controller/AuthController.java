package com.falcon.falcon.controller;

import com.falcon.falcon.DTOs.*;
import com.falcon.falcon.service.interfaces.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// this controller will cover auth related endpoints
// POST /auth/login application/json Credentials
// POST /auth/verification-codes application/json VerificationCodeRequest
// POST /auth/sign-up application/json SignUpRequest

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public String login(@RequestBody Credentials credentials) {
        // here we will use Spring security to authenticate the user
        // we will either return JWT or an HTTP error response
        String jwt = "test";
        return jwt;
    }

    @PostMapping("/verification-codes")
    public ResponseEntity<VerificationCodeResponse> sendVerificationCode(@RequestBody VerificationCodeRequest verificationCodeRequest) {
        VerificationCodeResponse verificationCodeResponse = this.authService.requestVerificationCode(verificationCodeRequest);
        return new ResponseEntity<>(verificationCodeResponse, HttpStatus.CREATED);
    }

    // POST /auth/signup
    /*{
       requestId,
       code,
       email,
       username,
       password
    }*/
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest signUpRequest) {
        SignUpResponse signUpResponse = this.authService.completeRegistration(signUpRequest);
        return new ResponseEntity<>(signUpResponse, HttpStatus.CREATED);
    }
}
