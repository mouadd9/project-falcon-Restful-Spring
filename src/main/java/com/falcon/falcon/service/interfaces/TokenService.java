package com.falcon.falcon.service.interfaces;

import com.falcon.falcon.model.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface TokenService {
    Map<String, String> generateToken(String subject, Collection<? extends GrantedAuthority> roles);
}
