package com.falcon.falcon.security;

import com.falcon.falcon.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// this class will provide a bean of a configured securityFilterChain, the filter chain is configured to validate the access-tokens for all the requests that require validation.
// we add a jwt validator to the filter chain.
// we aldo provide a decoder bean that uses the public key to verify if requests are valid. this bean is used by the OAuth2 resource server validator to check the validity of access-tokens
@Configuration
@EnableWebSecurity // this annotation triggers the configuration of Spring Security's web infrastructure and creates a security filter chain.
public class SecurityConfig {

    private final JwtDecoder jwtDecoder; // this will be used by the security filter chain to decode and validate access-token
    private final CustomUserDetailsService customUserDetailsService; // this will be used by teh authentication provider to load a user by username

    public SecurityConfig(JwtDecoder jwtDecoder, CustomUserDetailsService customUserDetailsService) {
        this.jwtDecoder = jwtDecoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    // this method will return a bean that will be managed by the IoC
    // The SecurityFilterChain bean is used to configure the security filter chain for HTTP requests. (the filter chain is a middleware that validates http requests)
    @Bean
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
                    ar.requestMatchers("/api/**").permitAll();
                    ar.anyRequest().authenticated();
                    System.out.println("Security configuration applied: /auth/** is permitted");
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder()); // this will be used to hash the password and compare it with the hashed one.
        return new ProviderManager(provider);
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
