package com.falcon.falcon.service.implementations;

import com.falcon.falcon.DTOs.*;
import com.falcon.falcon.exceptions.UserAlreadyExistsException;
import com.falcon.falcon.model.User;
import com.falcon.falcon.service.interfaces.*;
import org.antlr.v4.runtime.Token;
import org.springframework.stereotype.Service;

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
    public SignUpResponse completeRegistration(SignUpRequest request) {
        // Validate the verification code
        verificationService.validateVerificationCodeAgainstRedis(request); // this generates exceptions related to
        // Create the user
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(request.getEmail());
        userDTO.setUsername(request.getUsername());
        userDTO.setPassword(request.getPassword());

        // Save the user (this now includes roles in the UserDTO)
        UserDTO createdUser = userService.createUser(userDTO); // this should generate exceptions related to user creation

        // Generate a JWT token using the TokenService
        String jwt = tokenService.generateToken(
                createdUser.getUsername(), // Subject (e.g., username)
                createdUser.getRoles()     // Roles from the UserDTO
        );

        // Build the response
        SignUpResponse signUpResponse = new SignUpResponse();
        signUpResponse.setJwt(jwt);
        // Add more user details if needed
        return signUpResponse;
    }
}
