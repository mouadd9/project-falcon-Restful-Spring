package com.falcon.falcon.dtos.auth;

import lombok.Data;

@Data
public class LoginRequest {
    String username;
    String password;
}
