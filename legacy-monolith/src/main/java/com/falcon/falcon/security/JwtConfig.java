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
import org.springframework.security.oauth2.jwt.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// this class provide objects that encode and decode JWT tokens
// we inject RSA keys and parse them so they can be used to sign tokens by the encoder and to verify signatures by the decoder
@Configuration
public class JwtConfig {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    // the public key bean is in a pem format so we need to convert it from pem to a java RSAPublicKey object
    // we need a PEM parser this parser does the following :
    // - reads the PEM file, which usually has extensions like .pem
    // - The content of a PEM file is encoded in base64 and enclosed between -----BEGIN and -----END headers (e.g., -----BEGIN CERTIFICATE----- and -----END CERTIFICATE-----). The parser decodes this base64 content into its binary form.
    // - The parser extracts the actual data, such as an X.509 certificate, an RSA private key, or other cryptographic objects, from the decoded binary.
    // the constructor loads both the private and public .pem files and convert them to characters then it parses them and stores them in our local properties
    public JwtConfig(@Value("${jwt.private.key}") Resource privateKeyResource,
                     @Value("${jwt.public.key}") Resource publicKeyResource) throws Exception {

        try (Reader privateKeyReader = new InputStreamReader(privateKeyResource.getInputStream());
             Reader publicKeyReader = new InputStreamReader(publicKeyResource.getInputStream())
        ) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            // Load private key
            PEMParser pemParser = new PEMParser(privateKeyReader);
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            this.privateKey = (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
            pemParser.close();
            // Load public key
            try (PEMParser publicPemParser = new PEMParser(publicKeyReader)) {
                SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) publicPemParser.readObject();
                this.publicKey = (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
            }
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

    // asymmetric algorithm
    // When using RSA keys for JWT validation, the decoder only needs the public key to verify if a token is valid.
    // we will use the public key we injected and parsed.
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }


}
