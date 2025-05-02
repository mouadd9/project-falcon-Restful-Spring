package com.falcon.falcon.controller;

import com.falcon.falcon.dto.*;
import com.falcon.falcon.dto.authDto.LoginRequest;
import com.falcon.falcon.dto.authDto.SignUpRequest;
import com.falcon.falcon.dto.authDto.VerificationCodeRequest;
import com.falcon.falcon.dto.authDto.VerificationCodeResponse;
import com.falcon.falcon.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// this controller will cover auth related endpoints
// POST /auth/login application/json Credentials
// POST /auth/verification-codes application/json VerificationCodeRequest
// POST /auth/sign-up application/json SignUpRequest

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;
    private AuthenticationManager authenticationManager;


    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        // Type:  Interface (often implemented by ProviderManager)
        // Purpose: Manages the authentication process
        // Scope: High-level, delegates to providers ( providers are Low-level they do the actual authentication against a database)
        // Responsibility: Authentication Managers Decide Who authenticates (Authentication Providers Decide How to authenticate)
        this.authenticationManager = authenticationManager;
    }

    // login will authenticate the user and issue a jwt
    // we will inject an authentication service.
    // and we will inject a jwt generator service here.

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest credentials) {
        // we create an authentication token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword()
        );
        // we Authenticate with AuthenticationManager (that uses an Authentication provider in our case DaoAuthenticationProvider)
        Authentication result = authenticationManager.authenticate(authentication);

        // Set the authenticated user in the SecurityContext (optional for API)
        // SecurityContextHolder.getContext().setAuthentication(result);
        return new ResponseEntity<>(authService.generateAccessToken(result), HttpStatus.OK);
    }

    @PostMapping("/verification-codes")
    public ResponseEntity<VerificationCodeResponse> sendVerificationCode(@RequestBody VerificationCodeRequest verificationCodeRequest) {
        VerificationCodeResponse verificationCodeResponse = this.authService.requestVerificationCode(verificationCodeRequest);
        return new ResponseEntity<>(verificationCodeResponse, HttpStatus.CREATED);
    }

    // POST /auth/signup
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignUpRequest signUpRequest) {
        // verify request --> save new user
        UserDTO savedUser = this.authService.completeRegistration(signUpRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                signUpRequest.getUsername(),
                signUpRequest.getPassword()
        );
        // authenticate user
        Authentication result = authenticationManager.authenticate(authentication);

        return new ResponseEntity<>(authService.generateAccessToken(result), HttpStatus.CREATED);
    }
}
