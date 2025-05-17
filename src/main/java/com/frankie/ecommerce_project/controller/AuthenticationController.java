package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.authentication.request.*;
import com.frankie.ecommerce_project.dto.authentication.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frankie.ecommerce_project.service.AuthenticationService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {


    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.login(loginDto));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterDto registerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(registerDto));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.forgotPassword(forgotPasswordDto));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<ResendOtpResponse>> resendOtp(@RequestBody ResendOtpDto resendOtpDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.resendOtp(resendOtpDto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.resetPassword(resetPasswordDto));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<OtpVerificationResponse>> verifyOtp(@RequestBody OtpVerificationDto otpVerificationDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.verifyOtp(otpVerificationDto));
    }
}
