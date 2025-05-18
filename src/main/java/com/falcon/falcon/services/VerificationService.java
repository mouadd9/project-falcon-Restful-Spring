package com.falcon.falcon.services;

import com.falcon.falcon.dtos.authDto.SignUpRequest;
import com.falcon.falcon.dtos.authDto.VerificationEntry;
import com.falcon.falcon.exceptions.authExceptions.CodeExpiredException;
import com.falcon.falcon.exceptions.authExceptions.EmaiNotVerifiedOrRequestIdNotValid;
import com.falcon.falcon.exceptions.authExceptions.VerificationCodeInvalid;

public interface VerificationService {
    VerificationEntry generateVerificationEntry(String email);
    void storeRequest(VerificationEntry entry);
    void validateVerificationCodeAgainstRedis(SignUpRequest signUpRequest) throws CodeExpiredException, VerificationCodeInvalid, EmaiNotVerifiedOrRequestIdNotValid;
}
