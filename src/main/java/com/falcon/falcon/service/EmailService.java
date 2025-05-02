package com.falcon.falcon.service;

public interface EmailService {
    public void emailCode(String code, String expiryDate, String toEmail);
}
