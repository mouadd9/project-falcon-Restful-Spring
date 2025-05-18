package com.falcon.falcon.dtos.authDto;

import lombok.Data;

@Data
public class LoginRequest {
    String username;
    String password;
}
