package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.utils.VerificationType;

public interface EmailService {
    void sendOtpEmail(String to, String otp, VerificationType type);
}
