package com.falcon.falcon.service.interfaces;

import com.falcon.falcon.model.Role;

import java.util.Collection;
import java.util.Map;

public interface TokenService {
    String generateToken(String subject, Collection<Role> roles);
}
