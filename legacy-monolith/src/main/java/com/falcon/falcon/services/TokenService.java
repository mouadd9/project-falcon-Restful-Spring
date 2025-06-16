package com.falcon.falcon.services;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface TokenService {
    Map<String, String> generateToken(String subject,Long userId, Collection<? extends GrantedAuthority> roles);
}
