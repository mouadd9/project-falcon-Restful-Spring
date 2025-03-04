package com.falcon.falcon.service.interfaces;

public interface EmailService {
    public void emailCode(String code, String expiryDate, String toEmail);
}
