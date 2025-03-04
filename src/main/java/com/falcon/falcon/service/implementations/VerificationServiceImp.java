package com.falcon.falcon.service.implementations;

import com.falcon.falcon.DTOs.VerificationEntry;
import com.falcon.falcon.service.interfaces.VerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationServiceImp implements VerificationService {
    // acts as a bridge between your Java application and the Redis database
    private final RedisTemplate<String, Object> redisTemplate;
    private final int codeExpiryMinutes; // the number of minutes for the code validity
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // here we will inject the env variable dependency, environment variables declared in the properties file, are beans manages by the IoC
    // we can inject them into other IoC managed beans
    public VerificationServiceImp(@Value("${verification.code.expiry:10}") int codeExpiryMinutes, RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.codeExpiryMinutes = codeExpiryMinutes;
    }

    @Override
    public VerificationEntry generateVerificationEntry(String email) {
        // Check if an active verification entry already exists for this email
        String existingRequestId = (String) redisTemplate.opsForValue().get("verification:email:" + email);

        if (existingRequestId != null) {
            // Retrieve the existing entry
            VerificationEntry existingEntry = (VerificationEntry) redisTemplate.opsForValue().get("verification:" + existingRequestId);
            if (existingEntry != null) {
                // Debug logging
                System.out.println("Retrieved existing entry: " + existingEntry.getRequestId());
                System.out.println("Code: " + existingEntry.getVerificationCode());
                System.out.println("Expiry: " + existingEntry.getExpiryDate());
                // Return the existing entry instead of creating a new one
                return existingEntry;
            }
            // If we get here, the entry exists in the email index but not in the main storage
            // This indicates a data consistency issue, so we'll remove the stale reference
            redisTemplate.delete("verification:email:" + email);
        }

        // Create a new entry if no active one exists
        String requestId = UUID.randomUUID().toString();
        String verificationCode = generateRandomCode();
        // here we will create a string that will represent the date and time when the code will be expired
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(codeExpiryMinutes);
        String expiryDateStr = expiryDateTime.format(FORMATTER);
        return new VerificationEntry(requestId, email, verificationCode, expiryDateStr);
    }

    // this will write entries to Redis
    // we use the requestId as key
    public void storeRequest(VerificationEntry entry) {
        /* We will use the RedisTemplate instance template opsForValue() method to get an instance of ValueOperations,
        which provides methods to execute operations performed on simple values (or Strings in Redis terminology).
        The Redis SET method is implemented using the (you guessed it!) set() method, which takes a key name and a value. */
        /* the set method can take up to 4 arguments :
        *    1 - the key which will be used to retrieve values from Redis
        *    2 - the value object, what ae are storing
        *    3 - timeout 4 - time unit
        * */
        // Store using requestId as key for later verification
        redisTemplate.opsForValue().set(
                "verification:" + entry.getRequestId(),
                entry,
                codeExpiryMinutes,
                TimeUnit.MINUTES
        );

        // Also store using email to prevent multiple requests
        // this will be useful , in case a user already sent a verificationCodeRequest before, in this case well return back the already sent requestId
        // user 1 sent code request -> requesId and code generated all good, if the user goes to email and gets code and signs up then good, but if he resent a code request then we should only check if an entry is here is its here then we return a message prompying the user to check email
        redisTemplate.opsForValue().set(
                "verification:email:" + entry.getEmail(),
                entry.getRequestId(),
                codeExpiryMinutes,
                TimeUnit.MINUTES
        );


    }

    private String generateRandomCode() {
        // Generate a 6-digit verification code
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
}


/*
• How to configure the connection to Redis from the application ?
• How to access and configure the Spring Data RedisTemplate ?
• How to use opsForXXX to read and write data to Redis ?

Spring Data Redis provides access to Redis from Spring applications.
It offers both low-level and high-level abstractions for interacting with Redis.
*/