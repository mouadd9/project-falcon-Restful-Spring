package com.falcon.falcon.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.InputStreamReader;
import java.io.Reader;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// this class provides an object that encodes JWT
// it will be used to create JWTs
@Configuration
public class JwtConfiguration {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    // the constructor loads both the private and public .pem files and convert them to characters then it parses them and stores them in our local properties
    public JwtConfiguration(@Value("${jwt.private.key}") Resource privateKeyResource,
                            @Value("${jwt.public.key}") Resource publicKeyResource) throws Exception {
        try (Reader privateKeyReader = new InputStreamReader(privateKeyResource.getInputStream());
             Reader publicKeyReader = new InputStreamReader(publicKeyResource.getInputStream())) {

            // Load private key
            PEMParser pemParser = new PEMParser(privateKeyReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            this.privateKey = (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
            pemParser.close();

            // Load public key
            pemParser = new PEMParser(publicKeyReader);
            SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            this.publicKey = (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
        }
    }

    // this bean encodes a jwt using a public and private key.
    // this will be used when generating a jwt
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey)
                .privateKey(this.privateKey)
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

}
