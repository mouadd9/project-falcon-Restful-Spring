package com.falcon.falcon.dto.authDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class SignUpRequest {
    String requestId;
    String code;
    String email;
    String username;
    String password;
}
