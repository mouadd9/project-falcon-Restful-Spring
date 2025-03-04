package com.falcon.falcon.service.implementations;

import com.falcon.falcon.DTOs.*;
import com.falcon.falcon.exception.UserAlreadyExistsException;
import com.falcon.falcon.service.interfaces.AuthService;
import com.falcon.falcon.service.interfaces.EmailService;
import com.falcon.falcon.service.interfaces.UserService;
import com.falcon.falcon.service.interfaces.VerificationService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImp implements AuthService {
    private UserService userService;
    private EmailService emailService;
    private VerificationService verificationService;
    public AuthServiceImp(UserService userService, EmailService emailService, VerificationService verificationService) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationService = verificationService;
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
        // this.verificationService.validateVerificationCode(request);  if not valid (code expired, email mismatch, wrong code) we generate exception
        // this.userService.createUser(request); registersc the user
        // this.jwtService() generates a token
        SignUpResponse signUpResponse = new SignUpResponse();
        return signUpResponse;
    }
}
