package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.authentication.request.*;
import com.frankie.ecommerce_project.dto.authentication.response.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.frankie.ecommerce_project.service.AuthenticationService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

import java.util.List;

/**
 * Controller for handling authentication-related requests, OTP verification, and device session management.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int COOKIE_MAX_AGE_SECONDS = 30 * 24 * 60 * 60; // 30 days
    private static final String COOKIE_PATH = "/";

    private final AuthenticationService authenticationService;

    /**
     * Constructs AuthenticationController with the authentication service.
     *
     * @param authenticationService Service for handling authentication logic
     */
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Authenticates a user and returns access and refresh tokens, setting the refresh token in an HTTP-only cookie.
     *
     * @param loginDto Login credentials
     * @param request  HTTP request containing device information
     * @param response HTTP response for setting cookies
     * @return ResponseEntity with ApiResponse containing LoginResponse
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginDto loginDto,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response) {
        ApiResponse<LoginResponse> apiResponse = authenticationService.login(loginDto, request);

        setRefreshTokenCookie(apiResponse.getData().getRefreshToken(), response);

        return buildResponse(apiResponse);
    }

    /**
     * Registers a new user and sends an OTP for verification.
     *
     * @param registerDto Registration details
     * @return ResponseEntity with ApiResponse containing RegisterResponse
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterDto registerDto) {
        return buildResponse(authenticationService.register(registerDto));
    }

    /**
     * Initiates the forgot password process by sending an OTP.
     *
     * @param forgotPasswordDto Email for password reset
     * @return ResponseEntity with ApiResponse containing ForgotPasswordResponse
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        return buildResponse(authenticationService.forgotPassword(forgotPasswordDto));
    }

    /**
     * Resends an OTP for registration or password reset.
     *
     * @param resendOtpDto OTP resend details
     * @return ResponseEntity with ApiResponse containing ResendOtpResponse
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<ResendOtpResponse>> resendOtp(@RequestBody ResendOtpDto resendOtpDto) {
        return buildResponse(authenticationService.resendOtp(resendOtpDto));
    }

    /**
     * Resets a user's password after OTP verification.
     *
     * @param resetPasswordDto Password reset details
     * @return ResponseEntity with ApiResponse containing ResetPasswordResponse
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return buildResponse(authenticationService.resetPassword(resetPasswordDto));
    }

    /**
     * Verifies an OTP for registration or password reset.
     *
     * @param otpVerificationDto OTP verification details
     * @return ResponseEntity with ApiResponse containing OtpVerificationResponse
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<OtpVerificationResponse>> verifyOtp(@RequestBody OtpVerificationDto otpVerificationDto) {
        return buildResponse(authenticationService.verifyOtp(otpVerificationDto));
    }

    /**
     * Refreshes an access token using a refresh token from a cookie and updates the refresh token cookie.
     *
     * @param refreshToken Refresh token from a cookie
     * @param request      HTTP request containing device information
     * @param response     HTTP response for setting cookies
     * @return ResponseEntity with ApiResponse containing RefreshTokenResponse
     * @throws IllegalArgumentException If no refresh token is found
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@CookieValue("refreshToken") String refreshToken,
                                                                          HttpServletRequest request,
                                                                          HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("No refresh token found in cookies");
        }

        ApiResponse<RefreshTokenResponse> apiResponse = authenticationService.refreshToken(refreshToken, request);

        setRefreshTokenCookie(apiResponse.getData().getRefreshToken(), response);

        return buildResponse(apiResponse);
    }

    /**
     * Signs out a user, deactivates the device, and removes the refresh token.
     *
     * @param refreshToken Refresh token details
     * @return ResponseEntity with ApiResponse indicating success
     */
    @PostMapping("/signout")
    public ResponseEntity<ApiResponse<Void>> signout(@RequestBody RefreshTokenDto refreshToken) {
        return buildResponse(authenticationService.signout(refreshToken.getRefreshToken()));
    }

    /**
     * Retrieves active devices for a user, requiring authentication and permission check.
     *
     * @param email           User's email
     * @return ResponseEntity with ApiResponse containing a list of active devices
     */
    @GetMapping("/devices")
    public ResponseEntity<ApiResponse<List<DeviceInfoResponse>>> getActiveDevices(@RequestParam(name = "email") String email) {
        return buildResponse(authenticationService.getActiveDevices(email));
    }


    /**
     * Sets a refresh token in an HTTP-only cookie.
     *
     * @param refreshToken Refresh token value
     * @param response     HTTP response to add cookie
     */
    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        response.addCookie(cookie);
    }

    /**
     * Builds a ResponseEntity from an ApiResponse.
     *
     * @param <T>        Type of the response data
     * @param apiResponse ApiResponse containing data and status
     * @return ResponseEntity with status and body
     */
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> apiResponse) {
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }
}
