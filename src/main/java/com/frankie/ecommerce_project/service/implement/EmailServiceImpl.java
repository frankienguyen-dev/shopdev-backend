package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.service.EmailService;
import com.frankie.ecommerce_project.utils.VerificationType;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.resend.api-key}")
    private String resendApiKey;

    @Value("${spring.resend.from-email}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String to, String otp, VerificationType type) {
        try {
            // Đọc file HTML template
            ClassPathResource resource = new ClassPathResource("templates/otp-email.html");

            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            // Thay thế placeholder
            html = html.replace("${otp}", otp);

            Resend resend = new Resend(resendApiKey);
            String subject = type == VerificationType.OTP_REGISTER ? "OTP Registration Code" : "OTP Code Reset Password";

            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            resend.emails().send(options);
        } catch (ResendException | IOException e) {
            throw new RuntimeException("Lỗi gửi email OTP: " + e.getMessage());
        }
    }

}
