package com.falcon.falcon.service.interfaces;

import com.falcon.falcon.DTOs.SignUpRequest;
import com.falcon.falcon.DTOs.VerificationEntry;
import com.falcon.falcon.exceptions.CodeExpiredException;
import com.falcon.falcon.exceptions.EmaiNotVerifiedOrRequestIdNotValid;
import com.falcon.falcon.exceptions.VerificationCodeInvalid;

public interface VerificationService {
    VerificationEntry generateVerificationEntry(String email);
    void storeRequest(VerificationEntry entry);
    void validateVerificationCodeAgainstRedis(SignUpRequest signUpRequest) throws CodeExpiredException, VerificationCodeInvalid, EmaiNotVerifiedOrRequestIdNotValid;
}
