package com.falcon.falcon.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class VerificationCodeResponse {
    String requestId;
    String expiryDate;
}
