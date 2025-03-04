package com.falcon.falcon.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationEntry implements Serializable {
    private String requestId;
    private String email;
    private String verificationCode;
    private String expiryDate;
}

// objects of this class will be stored in memory caches that support serialization.