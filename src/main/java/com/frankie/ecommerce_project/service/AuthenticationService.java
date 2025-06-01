package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.authentication.request.*;
import com.frankie.ecommerce_project.dto.authentication.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AuthenticationService {
    ApiResponse<LoginResponse> login(LoginDto loginDto, HttpServletRequest request);

    ApiResponse<RegisterResponse> register(RegisterDto registerDto);

    ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordDto forgotPasswordDto);

    ApiResponse<ResendOtpResponse> resendOtp(ResendOtpDto resendOtpDto);

    ApiResponse<ResetPasswordResponse> resetPassword(ResetPasswordDto resetPasswordDto);

    ApiResponse<OtpVerificationResponse> verifyOtp(OtpVerificationDto otpVerificationDto);

    ApiResponse<RefreshTokenResponse> refreshToken(String refreshToken, HttpServletRequest request);

    ApiResponse<Void> signout(String refreshToken);

    ApiResponse<List<DeviceInfoResponse>> getActiveDevices(String email);
}
