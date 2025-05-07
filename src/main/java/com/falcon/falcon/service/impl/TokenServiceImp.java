package com.falcon.falcon.service.impl;

import com.falcon.falcon.service.TokenService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenServiceImp implements TokenService {
    private JwtEncoder jwtEncoder;

    public TokenServiceImp(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public Map<String, String> generateToken(String subject, Long userId, Collection<? extends GrantedAuthority> authorities){
        String scope = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // we issued this token
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(4, ChronoUnit.HOURS))
                .subject(subject)
                .claim("scope", scope)
                .claim("userId", userId.toString()) // Add the user ID as a claim
                .build();

        String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return Map.of("access-token", jwt);
    }
}
