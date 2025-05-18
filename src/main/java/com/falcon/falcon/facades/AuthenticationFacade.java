package com.falcon.falcon.facades;

import com.falcon.falcon.dtos.UserDTO;
import com.falcon.falcon.dtos.authDto.LoginRequest;
import com.falcon.falcon.dtos.authDto.SignUpRequest;
import com.falcon.falcon.dtos.authDto.VerificationCodeRequest;
import com.falcon.falcon.dtos.authDto.VerificationCodeResponse;
import org.springframework.security.core.Authentication;

import java.util.Map;

// The facade provides a clear, use-case oriented API for authentication
public interface AuthenticationFacade {
    /**
     * Initiates the registration process by sending a verification code to the user's email.
     *
     * @param request The verification code request containing the email
     * @return Response containing requestId and expiry date
     */
    VerificationCodeResponse initiateRegistration(VerificationCodeRequest request);

    /**
     * Completes the registration process by validating the verification code and creating the user.
     *
     * @param request The signup request containing user details and verification code
     * @return The created user
     */
    UserDTO completeRegistration(SignUpRequest request);

    /**
     * Authenticates a user with the provided credentials and generates an access token.
     *
     * @param credentials The login credentials
     * @return A map containing the access token
     */
    Map<String, String> authenticate(LoginRequest credentials);

    /**
     * Generates an access token for an authenticated user.
     *
     * @param authentication The authenticated user
     * @return A map containing the access token
     */
    Map<String, String> generateToken(Authentication authentication);
}
