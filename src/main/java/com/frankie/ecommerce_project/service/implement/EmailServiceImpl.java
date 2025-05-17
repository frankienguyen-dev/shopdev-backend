package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.service.EmailService;
import com.frankie.ecommerce_project.utils.VerificationType;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.resend.api-key}")
    private String resendApiKey;

    @Value("${spring.resend.from-email}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String to, String otp, VerificationType type) {
        try {
            Resend resend = new Resend(resendApiKey);
            String subject = type == VerificationType.OTP_REGISTER ? "OTP Registration Code" : "OTP Code Reset Password";
            String text = "Your OTP code is " + otp + ". Effective for 5 minutes.";

            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .text(text)
                    .build();

            CreateEmailResponse response = resend.emails().send(options);
        } catch (ResendException e) {
            throw new RuntimeException("Lỗi gửi email OTP: " + e.getMessage());
        }
    }

}
