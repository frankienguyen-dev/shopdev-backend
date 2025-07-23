package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.authentication.request.*;
import com.frankie.ecommerce_project.dto.authentication.response.*;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.model.*;
import com.frankie.ecommerce_project.repository.*;
import com.frankie.ecommerce_project.security.token.JwtTokenProvider;
import com.frankie.ecommerce_project.service.AuthenticationService;
import com.frankie.ecommerce_project.service.EmailService;
import com.frankie.ecommerce_project.utils.VerificationType;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for user authentication, handling login, registration, OTP verification,
 * and multi-device session management.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final int OTP_EXPIRATION_SECONDS = 300; // 5 phút
    private static final int MAX_OTP_ATTEMPTS = 5; // Số lần thử OTP tối đa
    private static final long REFRESH_TOKEN_EXPIRY_SECONDS = 30L * 24 * 60 * 60; // 30 days
    private static final String ROLE_USER = "ROLE_USER";
    private static final String SUCCESS_LOGIN = "Login successful";
    private static final String SUCCESS_OTP_VERIFIED = "OTP verification successful";
    private static final String SUCCESS_PASSWORD_RESET = "Password reset successful";
    private static final String SUCCESS_OTP_SENT = "OTP has been sent to your email";
    private static final String SUCCESS_REGISTRATION = "Registration successful, please check your email for OTP";

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider securityUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final DeviceRepository deviceRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Constructs AuthenticationServiceImpl with required dependencies.
     *
     * @param authenticationManagerBuilder Authentication manager for user verification
     * @param securityUtils                Utility for JWT token creation
     * @param userRepository               Repository for user data access
     * @param passwordEncoder              Encoder for password hashing
     * @param verificationCodeRepository   Repository for OTP verification codes
     * @param refreshTokenRepository       Repository for refresh tokens
     * @param deviceRepository             Repository for user devices
     * @param emailService                 Service for sending emails
     * @param roleRepository               Repository for user roles
     */
    public AuthenticationServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder,
            JwtTokenProvider securityUtils,
            UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService,
            VerificationCodeRepository verificationCodeRepository, RoleRepository roleRepository,
            DeviceRepository deviceRepository, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtils = securityUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeRepository = verificationCodeRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.deviceRepository = deviceRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Authenticates a user, creates JWT and refresh tokens, and stores device information.
     *
     * @param loginDto Login credentials (email, password)
     * @param request  HTTP request containing device information
     * @return ApiResponse containing LoginResponse with access and refresh tokens
     * @throws IllegalArgumentException If email or password is invalid
     * @throws IllegalStateException   If the account is not verified
     */
    @Transactional
    @Override
    public ApiResponse<LoginResponse> login(LoginDto loginDto, HttpServletRequest request) {
        User user = validateUserCredentials(loginDto.getEmail(), loginDto.getPassword());
        if (!user.getIsVerified()) {
            throw new IllegalStateException("Account not verified, please verify OTP via email or request a new OTP");
        }
        Authentication authentication = authenticateUser(loginDto.getEmail(), loginDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = securityUtils.createAccessToken(authentication);

        String userAgent = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent") : "Unknown";
        String ipAddress = request.getRemoteAddr() != null ? request.getRemoteAddr() : "Unknown";

        Device device = deviceRepository.findByUserAndUserAgent(user, userAgent)
                .orElseGet(() -> Device.builder()
                        .user(user)
                        .userAgent(userAgent)
                        .ip(ipAddress)
                        .lastActive(Instant.now())
                        .isActive(true)
                        .createdAt(Instant.now())
                        .build());
        device.setLastActive(Instant.now());
        device.setIsActive(true);
        deviceRepository.save(device);
        deviceRepository.flush();

        refreshTokenRepository.deleteByUserAndDevice(user, device);


        String refreshToken = securityUtils.createRefreshToken(user.getEmail());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .device(device)
                .token(refreshToken)
                .expiredAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS))
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        LoginResponse loginResponse = LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken)
                .build();
        return ApiResponse.success(SUCCESS_LOGIN, HttpStatus.OK, loginResponse);
    }


    /**
     * Refreshes an access token using a valid refresh token.
     *
     * @param refreshToken Current refresh token
     * @param request      HTTP request containing device information
     * @return ApiResponse containing RefreshTokenResponse with new access and refresh tokens
     * @throws IllegalArgumentException If the refresh token is invalid
     * @throws IllegalStateException   If the refresh token is expired
     */
    @Transactional
    @Override
    public ApiResponse<RefreshTokenResponse> refreshToken(String refreshToken, HttpServletRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (token.getExpiredAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Refresh token is expired");
        }
        User user = token.getUser();
        Device device = token.getDevice();
        String ipAddress = request.getRemoteAddr() != null ? request.getRemoteAddr() : "Unknown";
        device.setLastActive(Instant.now());
        device.setIp(ipAddress);
        deviceRepository.save(device);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null,
                getAuthorities(user));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String newAccessToken = securityUtils.createAccessToken(authentication);

        String newRefreshToken = securityUtils.createRefreshToken(user.getEmail());
        token.setToken(newRefreshToken);
        token.setExpiredAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRY_SECONDS));
        token.setCreatedAt(Instant.now());
        refreshTokenRepository.save(token);

        RefreshTokenResponse response = RefreshTokenResponse.builder().accessToken(newAccessToken)
                .refreshToken(newRefreshToken).build();

        return ApiResponse.success("Token refreshed successfully", HttpStatus.OK, response);
    }

    /**
     * Signs out a user, deactivates the device, and deletes the refresh token.
     *
     * @param refreshToken Refresh token of the session
     * @return ApiResponse indicating successful sign-out
     * @throws IllegalArgumentException If the refresh token is invalid
     */
    @Transactional
    @Override
    public ApiResponse<Void> signout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        Device device = token.getDevice();
        device.setIsActive(false);
        deviceRepository.save(device);
        refreshTokenRepository.delete(token);
        return ApiResponse.success("Signed out successfully", HttpStatus.OK, null);
    }

    /**
     * Retrieves the list of active devices for a user.
     *
     * @param email User's email address
     * @return ApiResponse containing a list of active devices
     * @throws ResourceNotFoundException If the user is not found
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<DeviceInfoResponse>> getActiveDevices(String email) {
         User user = userRepository.findByEmailWithRoles(email)
                 .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
         List<Device> devices = deviceRepository.findByUserAndIsActiveTrue(user);
         List<DeviceInfoResponse> deviceInfoResponses = devices.stream()
                 .map(device -> {
                     Optional<RefreshToken> refreshToken = refreshTokenRepository.findByDevice(device);
                     return DeviceInfoResponse.builder()
                             .userAgent(device.getUserAgent())
                             .ip(device.getIp())
                             .lastActive(device.getLastActive())
                             .isActive(device.getIsActive())
                             .createdAt(device.getCreatedAt())
                             .tokenExpiry(refreshToken.map(RefreshToken::getExpiredAt).orElse(null))
                             .build();
                 }).collect(Collectors.toList());
        return ApiResponse.success("Active devices retrieved successfully", HttpStatus.OK, deviceInfoResponses);
    }

    /**
     * Registers a new user and sends an OTP for verification.
     *
     * @param registerDto Registration details
     * @return ApiResponse containing RegisterResponse
     * @throws IllegalStateException If email is already registered
     */
    @Transactional
    @Override
    public ApiResponse<RegisterResponse> register(RegisterDto registerDto) {
        validateEmailNotRegistered(registerDto.getEmail());
        validatePasswordMatch(registerDto.getPassword(), registerDto.getConfirmPassword());
        User newUser = createNewUser(registerDto);
        userRepository.save(newUser);
        RegisterResponse response = RegisterResponse.builder()
                .email(newUser.getEmail())
                .fullName(newUser.getFullName())
                .build();
        return createOrUpdateOtp(newUser, VerificationType.OTP_REGISTER, response, true);
    }

    /**
     * Verifies an OTP for registration or password reset.
     *
     * @param otpVerificationDto OTP verification details
     * @return ApiResponse containing OtpVerificationResponse
     * @throws ResourceNotFoundException If user or OTP is not found
     * @throws IllegalStateException     If OTP is invalid, expired, or max attempts exceeded
     */
    @Transactional
    @Override
    public ApiResponse<OtpVerificationResponse> verifyOtp(OtpVerificationDto otpVerificationDto) {
        User user = findUserByEmail(otpVerificationDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", otpVerificationDto.getEmail()));
        VerificationType verificationType = validateVerificationType(otpVerificationDto.getType());
        VerificationCode verificationCode = findVerificationCode(user, verificationType)
                .orElseThrow(
                        () -> new ResourceNotFoundException("OTP not found: ", "OTP", otpVerificationDto.getOtp()));

        if (verificationCode.getAttempts() >= MAX_OTP_ATTEMPTS) {
            throw new IllegalStateException("Too many OTP attempts");
        }

        if (isOtpExpired(verificationCode)) {
            throw new IllegalStateException("OTP has expired, please request a new OTP");
        }

        if (!passwordEncoder.matches(otpVerificationDto.getOtp(), verificationCode.getHashedCode())) {
            verificationCode.setAttempts(verificationCode.getAttempts() + 1);
            verificationCodeRepository.save(verificationCode);
            throw new IllegalStateException("Invalid OTP");
        }

        verificationCode.setIsVerified(true);
        verificationCodeRepository.save(verificationCode);

        if (VerificationType.OTP_REGISTER.equals(verificationType)) {
            user.setIsVerified(true);
            userRepository.save(user);
            verificationCodeRepository.delete(verificationCode);
        }

        OtpVerificationResponse response = OtpVerificationResponse.builder()
                .message("OTP verification successful")
                .build();

        return ApiResponse.success(SUCCESS_OTP_VERIFIED, HttpStatus.OK, response);
    }

    /**
     * Initiates the forgot password process by sending an OTP.
     *
     * @param forgotPasswordDto Forgot password details
     * @return ApiResponse containing ForgotPasswordResponse
     * @throws ResourceNotFoundException If user is not found
     */
    @Transactional
    @Override
    public ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = findUserByEmail(forgotPasswordDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", forgotPasswordDto.getEmail()));

        VerificationType verificationType = validateVerificationType(VerificationType.OTP_FORGOT_PASSWORD.name());

        ForgotPasswordResponse response = ForgotPasswordResponse.builder()
                .message("OTP has been sent to your email")
                .build();

        return createOrUpdateOtp(user, verificationType, response, false);
    }

    /**
     * Resends an OTP for registration or password reset.
     *
     * @param resendOtpDto Resend OTP details
     * @return ApiResponse containing ResendOtpResponse
     * @throws ResourceNotFoundException If user is not found
     * @throws IllegalStateException     If OTP cannot be resent
     */
    @Transactional
    @Override
    public ApiResponse<ResendOtpResponse> resendOtp(ResendOtpDto resendOtpDto) {
        User user = findUserByEmail(resendOtpDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email", "email", resendOtpDto.getEmail()));

        VerificationType verificationType = validateVerificationType(resendOtpDto.getType());

        if (VerificationType.OTP_REGISTER.equals(verificationType) && user.getIsVerified()) {
            throw new IllegalStateException("User already verified, cannot resend OTP for registration");
        }
        if (VerificationType.OTP_FORGOT_PASSWORD.equals(verificationType)) {
            if (!user.getIsVerified()) {
                throw new IllegalStateException("User not verified, cannot resend OTP for forgot password");
            }
            Optional<VerificationCode> existingCode = findVerificationCode(user, verificationType);
            if (existingCode.isEmpty()) {
                throw new IllegalStateException(
                        "No active forgot password OTP found, please initiate forgot password process first");
            }
        }

        ResendOtpResponse response = ResendOtpResponse.builder()
                .message("OTP has been sent to your email")
                .build();

        return createOrUpdateOtp(user, verificationType, response, false);
    }

    /**
     * Resets a user's password after OTP verification.
     *
     * @param resetPasswordDto Reset password details
     * @return ApiResponse containing ResetPasswordResponse
     * @throws ResourceNotFoundException If user or OTP is not found
     * @throws IllegalStateException     If OTP is not verified or expired
     */
    @Transactional
    @Override
    public ApiResponse<ResetPasswordResponse> resetPassword(ResetPasswordDto resetPasswordDto) {
        User user = findUserByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", resetPasswordDto.getEmail()));

        VerificationCode verificationCode = findVerificationCode(user, VerificationType.OTP_FORGOT_PASSWORD)
                .orElseThrow(() -> new IllegalStateException("No valid OTP found for password reset"));

        if (!verificationCode.getIsVerified()) {
            throw new IllegalStateException("OTP not verified");
        }

        if (isOtpExpired(verificationCode)) {
            throw new IllegalStateException("OTP has expired, please request a new OTP");
        }

        validatePasswordMatch(resetPasswordDto.getPassword(), resetPasswordDto.getConfirmPassword());
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        user.setUpdatedBy(user.getEmail());
        userRepository.save(user);
        verificationCodeRepository.delete(verificationCode);

        ResetPasswordResponse response = ResetPasswordResponse.builder()
                .message(SUCCESS_PASSWORD_RESET)
                .build();

        return ApiResponse.success("Password reset successful", HttpStatus.OK, response);
    }

    /**
     * Creates or updates an OTP for a user and sends it via email.
     *
     * @param user             User associated with the OTP
     * @param verificationType OTP type (e.g., OTP_REGISTER, OTP_FORGOT_PASSWORD)
     * @param response         Response object to return
     * @param isNewUser        Indicates if this is a new user registration
     * @param <T>              Generic type for response
     * @return ApiResponse containing the response object
     * @throws IllegalStateException If the user's state is invalid for the OTP type
     */
    @Transactional
    protected  <T> ApiResponse<T> createOrUpdateOtp(User user, VerificationType verificationType, T response, boolean isNewUser) {

        validateUserForOtp(user, verificationType);

        String otp = generateOtp();
        String hashedOtp = passwordEncoder.encode(otp);

        VerificationCode verificationCode = findVerificationCode(user, verificationType)
                .orElseGet(() -> VerificationCode.builder()
                        .user(user)
                        .type(verificationType)
                        .isVerified(false)
                        .build());
        verificationCode.setHashedCode(hashedOtp);
        verificationCode.setAttempts(0);
        verificationCode.setExpirationTime(Instant.now().plusSeconds(OTP_EXPIRATION_SECONDS));
        verificationCodeRepository.save(verificationCode);

        emailService.sendOtpEmail(user.getEmail(), otp, verificationType);

        String message = VerificationType.OTP_REGISTER.equals(verificationType) ? SUCCESS_REGISTRATION
                : SUCCESS_OTP_SENT;
        HttpStatus httpStatus = (VerificationType.OTP_REGISTER.equals(verificationType) && isNewUser)
                ? HttpStatus.CREATED
                : HttpStatus.OK;

        return ApiResponse.success(message, httpStatus, response);

    }

    /**
     * Generates a random 6-digit OTP.
     *
     * @return Generated OTP as a string
     */
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * Validates user credentials.
     *
     * @param email    User's email
     * @param password User's password
     * @return Validated User object
     * @throws IllegalArgumentException If credentials are invalid
     */
    private User validateUserCredentials(String email, String password) {
        Optional<User> findUser = findUserByEmail(email);
        return findUser.filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }

    /**
     * Finds a user by email.
     *
     * @param email User's email
     * @return Optional containing the User, if found
     */
    private Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmailWithRoles(email);
    }

    /**
     * Authenticates a user with provided credentials.
     *
     * @param email    User's email
     * @param password User's password
     * @return Authentication object
     */
    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password);
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    /**
     * Validates that an email is not already registered.
     *
     * @param email Email to check
     * @throws IllegalStateException If email is registered or not verified
     */
    private void validateEmailNotRegistered(String email) {
        Optional<User> existingUser = findUserByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsVerified())
                throw new IllegalStateException("Email has been registered and verified");
            throw new IllegalStateException("User not verified, cannot create OTP for forgot password");
        }
    }

    /**
     * Validates that password and confirmation password match.
     *
     * @param password        Password
     * @param confirmPassword Confirmation password
     * @throws IllegalArgumentException If passwords do not match
     */
    private void validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new IllegalArgumentException("The password and confirmation password do not match");
    }

    /**
     * Creates a new user from registration details.
     *
     * @param registerDto Registration details
     * @return Created a User object
     * @throws ResourceNotFoundException If the default role is not found
     */
    private User createNewUser(RegisterDto registerDto) {
        Role role = roleRepository.findByNameWithPermissions(ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", ROLE_USER));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        return User.builder()
                .fullName(registerDto.getFullName())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(roles)
                .isActive(true)
                .isDeleted(false)
                .createdBy(registerDto.getEmail())
                .isVerified(false)
                .build();
    }

    /**
     * Validates the OTP verification type.
     *
     * @param type Verification type
     * @return Valid VerificationType enum
     * @throws IllegalArgumentException If type is invalid
     */
    private VerificationType validateVerificationType(String type) {
        try {
            return VerificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid verification type: " + type);
        }
    }

    /**
     * Finds a verification code for a user and type.
     *
     * @param user User associated with the OTP
     * @param type Verification type
     * @return Optional containing the VerificationCode, if found
     */
    private Optional<VerificationCode> findVerificationCode(User user, VerificationType type) {
        return verificationCodeRepository.findByUserAndType(user, type);
    }

    /**
     * Checks if an OTP has expired.
     *
     * @param verificationCode Verification code to check
     * @return True if expired, false otherwise
     */
    private boolean isOtpExpired(VerificationCode verificationCode) {
        return verificationCode.getExpirationTime().isBefore(Instant.now());
    }

    /**
     * Validates user eligibility for OTP creation.
     *
     * @param user User to validate
     * @param type Verification type
     * @throws IllegalStateException If user state is invalid
     */
    private void validateUserForOtp(User user, VerificationType type) {
        if (VerificationType.OTP_REGISTER.equals(type) && user.getIsVerified()) {
            throw new IllegalStateException("User already verified, cannot create OTP for registration");
        }
        if (VerificationType.OTP_FORGOT_PASSWORD.equals(type) && !user.getIsVerified()) {
            throw new IllegalStateException("User not verified, cannot create OTP for forgot password");
        }
    }

    /**
     * Retrieves authorities for a user based on their roles and permissions.
     *
     * @param user User to retrieve authorities for
     * @return Collection of GrantedAuthority objects containing permissions
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());
    }
}
