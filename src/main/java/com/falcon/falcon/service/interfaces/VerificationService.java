package com.falcon.falcon.service.interfaces;

import com.falcon.falcon.DTOs.VerificationEntry;

public interface VerificationService {
    VerificationEntry generateVerificationEntry(String email);
    public void storeRequest(VerificationEntry entry);
}
