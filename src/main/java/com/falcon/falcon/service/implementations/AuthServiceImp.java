package com.falcon.falcon.service.implementations;

import com.falcon.falcon.DTOs.*;
import com.falcon.falcon.exceptions.UserAlreadyExistsException;
import com.falcon.falcon.service.interfaces.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImp implements AuthService {
    private UserService userService;
    private EmailService emailService;
    private VerificationService verificationService;
    private TokenService tokenService;

    public AuthServiceImp(UserService userService, EmailService emailService, VerificationService verificationService, TokenService tokenService) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationService = verificationService;
        this.tokenService = tokenService;
    }
    // this method will be used to check if the email to verify isnt taken.
    // then it generates a request ID a verification code and stores everything in Redis
    // then it sends an email with the code
    @Override
    public VerificationCodeResponse requestVerificationCode(VerificationCodeRequest request) throws UserAlreadyExistsException {
        // Validate email doesn't exist
        this.userService.validateEmailNotExists(request.getEmail());
        // Generate verification entry
        VerificationEntry entry = this.verificationService.generateVerificationEntry(request.getEmail());
        // Store in Redis
        this.verificationService.storeRequest(entry);
        this.emailService.emailCode(
                entry.getVerificationCode(),
                entry.getExpiryDate(),
                request.getEmail()
        );
        return new VerificationCodeResponse(entry.getRequestId(), entry.getExpiryDate());
    }

    @Override
    public UserDTO completeRegistration(SignUpRequest request) {
        // Validate the verification code
        verificationService.validateVerificationCodeAgainstRedis(request); // this generates exceptions related to
        // Create the user
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(request.getEmail());
        userDTO.setUsername(request.getUsername());
        userDTO.setPassword(request.getPassword());

        // Save the user (this now includes roles in the UserDTO)
        UserDTO createdUser = userService.createUser(userDTO); // this should generate exceptions related to user creation
        return createdUser;
    }

    @Override
    public Map<String, String> generateAccessToken(Authentication authentication) {
        return this.tokenService.generateToken(authentication.getName(), authentication.getAuthorities());
    }
}
