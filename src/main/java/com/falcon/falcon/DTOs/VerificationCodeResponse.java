package com.falcon.falcon.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class VerificationCodeResponse {
    String requestId;
    String expiryDate;
}
