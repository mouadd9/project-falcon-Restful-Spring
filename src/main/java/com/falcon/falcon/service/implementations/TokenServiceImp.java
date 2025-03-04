package com.falcon.falcon.service.implementations;

import com.falcon.falcon.model.Role;
import com.falcon.falcon.service.interfaces.TokenService;
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
    public String generateToken(String subject, Collection<Role> roles){
        String scope = roles.stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("self") // we issued this token
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                    .subject(subject)
                    .claim("scope", scope)
                    .build();

            return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
