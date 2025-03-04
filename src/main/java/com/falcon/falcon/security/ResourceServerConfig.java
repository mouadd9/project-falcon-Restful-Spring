package com.falcon.falcon.security;

import org.springframework.core.io.Resource;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.security.interfaces.RSAPublicKey;

// this class will provide a bean of a configured securityFilterChain, the filter chain is configured to validate the access-tokens for all the requests that require validation.
// we add a jwt validator to the filter chain.
// we aldo provide a decoder bean that uses the public key to verify if requests are valid. this bean is used by the OAuth2 resource server validator to check the validity of access-tokens
@Configuration
@EnableWebSecurity // this annotation triggers the configuration of Spring Security's web infrastructure and creates a security filter chain.
public class ResourceServerConfig {
    // this public key is a bean defined in the properties file
    // it will be used be the jwt decoder to check if the jwt is valid.
    private final RSAPublicKey publicKey;

    // the public key bean is in a pem format so we need to convert it from pem to a java RSAPublicKey object
    // we need a PEM parser this parser does the following :
      // - reads the PEM file, which usually has extensions like .pem
      // - The content of a PEM file is encoded in base64 and enclosed between -----BEGIN and -----END headers (e.g., -----BEGIN CERTIFICATE----- and -----END CERTIFICATE-----). The parser decodes this base64 content into its binary form.
      // - The parser extracts the actual data, such as an X.509 certificate, an RSA private key, or other cryptographic objects, from the decoded binary.
    public ResourceServerConfig(@Value("${jwt.public.key}") Resource publicKeyResource) throws Exception {
        // the idea here is, we usually store keys in pem files, so we need a way to convert data in those files into readable characters
        // because those readable characters will be parsed by a pem parser
        // so we will do the following :
        //   - Open the resource as an InputStream. (.getInputStream())
        //   - Converts the byte stream into a character stream using InputStreamReader.
        //   - Allows the program to read the PEM content as text using the Reader object.
        try (Reader publicKeyReader = new InputStreamReader(publicKeyResource.getInputStream())) { //  here we convert raw bytes into characters using a specified character encoding (e.g., UTF-8, ASCII).
            // now we will parse the PEM
            PEMParser pemParser = new PEMParser(publicKeyReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            SubjectPublicKeyInfo publicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            this.publicKey = (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
        }
    }
    // this method will return a bean that will be managed by the IoC
    @Bean // The SecurityFilterChain bean is used to configure the security filter chain for HTTP requests. (the filter chain is a middleware that validates http requests)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http     // this configures spring security not to create server side sessions for Http requests (by default spring security creates sessions upon receiving Http requests)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable) // Stateless token based authentication do not require csrf tokens, because we don't authenticate using browser cookies, we retrieve a token from the local storage and put in the authorization header
                .cors(Customizer.withDefaults()) // requests can be made from different origins right now (our front end app)
                // we are not gonna manually create a token validator !
                // we will set up our application as an OAuth2 resource server that validates JWT tokens
                // an OAuth2 resource server is responsible for serving protected resources (API endpoints)
                // it validates the access-token included in the Authorization header of incoming requests.
                // we will use our own decoder, a jwt decoder is configured with a public key that we generated so that we can know if a jwt was signed by our app or not, now whenever a request comes, OAuth2 will take the jwt and then use the decoder
                // the Decoder will take the signature of the jwt, decrypt it using the public key -> get the original hash = hashed (Header + payload)
                // then it will hash the current Header and payload , and compare, if the decrypted signature is equal to the hashed header and payload then this token was signed using the correct private key
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(ar -> {
                    ar.requestMatchers("/auth/**").permitAll();
                    ar.anyRequest().authenticated();
                    System.out.println("Security configuration applied: /auth/** is permitted");
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

        return http.build();
    }

    // asymmetric algorithm
    // When using RSA keys for JWT validation, the decoder only needs the public key to verify if a token is valid.
    // we will use the public key we injected and parsed.
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ // 2
        return new BCryptPasswordEncoder();
    }

    // handling requests coming from browsers with CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {


        /*Cross-Origin Resource Sharing (CORS) is a security
         feature that allows browser-based requests using AJAX.
         CORS helps keep web interactions secure while allowing
         necessary communication between different websites.*/


        CorsConfiguration corsConfiguration = new CorsConfiguration(); // a class provided by Spring that holds the CORS configuration settings
        // we will allow all origins to access our apis
        corsConfiguration.addAllowedOrigin("*");
        // This allows all HTTP methods (GET, POST, PUT, DELETE, etc.) from any origin to be executed.
        corsConfiguration.addAllowedMethod("*");
        // This allows any HTTP header to be included in the request from the client.
        corsConfiguration.addAllowedHeader("*");

        // corsConfiguration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }



}
