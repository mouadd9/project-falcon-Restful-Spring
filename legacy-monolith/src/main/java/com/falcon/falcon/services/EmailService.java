package com.falcon.falcon.services;

public interface EmailService {
    public void emailCode(String code, String expiryDate, String toEmail);
}
