package com.falcon.falcon.service.impl;

import com.falcon.falcon.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailServiceImp(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void emailCode(String code, String expiryDate, String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Verification Code");
        message.setText("Your verification code is: " + code + "\n" +
                "This code will expire at: " + expiryDate);

        mailSender.send(message);
    }
}