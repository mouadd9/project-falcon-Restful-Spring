package com.falcon.falcon.facades.impl;

import com.falcon.falcon.dtos.UserDTO;
import com.falcon.falcon.dtos.authDto.*;
import com.falcon.falcon.facades.AuthenticationFacade;
import com.falcon.falcon.services.EmailService;
import com.falcon.falcon.services.TokenService;
import com.falcon.falcon.services.UserService;
import com.falcon.falcon.services.VerificationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthenticationFacadeImpl implements AuthenticationFacade {
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationFacadeImpl(
            UserService userService,
            EmailService emailService,
            VerificationService verificationService,
            TokenService tokenService,
            AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationService = verificationService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public VerificationCodeResponse initiateRegistration(VerificationCodeRequest request) {
        // Validate email doesn't exist
        this.userService.validateEmailNotExists(request.getEmail());

        // Generate verification entry
        VerificationEntry entry = this.verificationService.generateVerificationEntry(request.getEmail());

        // Store in Redis
        this.verificationService.storeRequest(entry);

        // Send email
        this.emailService.emailCode(
                entry.getVerificationCode(),
                entry.getExpiryDate(),
                request.getEmail()
        );

        return new VerificationCodeResponse(entry.getRequestId(), entry.getExpiryDate());
    }

    @Override
    @Transactional
    public UserDTO completeRegistration(SignUpRequest request) {
        // Validate the verification code
        verificationService.validateVerificationCodeAgainstRedis(request);

        // Create the user
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(request.getEmail());
        userDTO.setUsername(request.getUsername());
        userDTO.setPassword(request.getPassword());

        // Save the user
        return userService.createUser(userDTO);
    }

    @Override
    public Map<String, String> authenticate(LoginRequest credentials) {
        // Create authentication token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword()
        );

        // Authenticate
        Authentication result = authenticationManager.authenticate(authentication);

        // Generate token
        return generateToken(result);
    }

    @Override
    public Map<String, String> generateToken(Authentication authentication) {
        // get user
        UserDTO user = userService.getUserByUsername(authentication.getName());

        // Generate token
        return tokenService.generateToken(
                authentication.getName(),
                user.getId(),
                authentication.getAuthorities()
        );
    }
}
