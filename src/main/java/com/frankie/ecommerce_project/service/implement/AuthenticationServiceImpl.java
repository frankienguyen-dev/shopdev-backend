package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.authentication.request.*;
import com.frankie.ecommerce_project.dto.authentication.response.*;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.model.Role;
import com.frankie.ecommerce_project.model.User;
import com.frankie.ecommerce_project.model.VerificationCode;
import com.frankie.ecommerce_project.repository.RoleRepository;
import com.frankie.ecommerce_project.repository.UserRepository;
import com.frankie.ecommerce_project.repository.VerificationCodeRepository;
import com.frankie.ecommerce_project.security.token.JwtTokenProvider;
import com.frankie.ecommerce_project.service.AuthenticationService;
import com.frankie.ecommerce_project.service.EmailService;
import com.frankie.ecommerce_project.utils.VerificationType;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider securityUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;

    private static final int OTP_EXPIRATION_SECONDS = 300; // 5 phút
    private final RoleRepository roleRepository;

    public AuthenticationServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, JwtTokenProvider securityUtils,
                                     UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService,
                                     VerificationCodeRepository verificationCodeRepository, RoleRepository roleRepository) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtils = securityUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeRepository = verificationCodeRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginDto loginDto) {
        try {
            Optional<User> userLogin = userRepository.findByEmail(loginDto.getEmail());
            if (userLogin.isEmpty()
                    || !passwordEncoder.matches(loginDto.getPassword(), userLogin.get().getPassword())) {
                return ApiResponse.error("Email or password is incorrect", HttpStatus.BAD_REQUEST, null);
            }
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject()
                    .authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = securityUtils.createToken(authentication);
            LoginResponse loginResponse = new LoginResponse(accessToken);
            return ApiResponse.success("Login successfully", HttpStatus.OK, loginResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ApiResponse<RegisterResponse> register(RegisterDto registerDto) {
        try {
            Optional<User> existingUser = userRepository.findByEmail(registerDto.getEmail());

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                if (user.getIsVerified()) {
                    return ApiResponse.error("Email has been registered and verified", HttpStatus.BAD_REQUEST, null);
                }
                return createOrUpdateOtp(user, VerificationType.OTP_REGISTER, RegisterResponse.builder()
                        .email(user.getEmail()).fullName(user.getFullName()).build());
            }

            // Kiểm tra type hợp lệ
            VerificationType verificationType = VerificationType.OTP_REGISTER;
            try {
                VerificationType.valueOf(verificationType.name());
            } catch (IllegalArgumentException e) {
                return ApiResponse.error("Invalid verification type", HttpStatus.BAD_REQUEST, null);
            }

            // Gán vai trò mặc định (USER)
            Optional<Role> findRole = roleRepository.findByName("ROLE_USER");
            if (findRole.isEmpty()) {
                return ApiResponse.error("Role not found", HttpStatus.BAD_REQUEST, null);
            }
            Set<Role> roles = new HashSet<>();
            roles.add(findRole.get());

            // Tạo người dùng mới
            if(!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
                return ApiResponse.error("The password and confirmation password do not match.", HttpStatus.BAD_REQUEST, null);
            }

            User user = User.builder()
                    .fullName(registerDto.getFullName())
                    .email(registerDto.getEmail())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .roles(roles)
                    .isActive(true)
                    .isDeleted(false)
                    .createdBy(registerDto.getEmail())
                    .isVerified(false)
                    .build();

            userRepository.save(user);

            return createOrUpdateOtp(user, VerificationType.OTP_REGISTER, RegisterResponse.builder()
                    .email(user.getEmail()).fullName(user.getFullName()).build());
        } catch (Exception e) {
            return ApiResponse.error("Error " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ApiResponse<OtpVerificationResponse> verifyOtp(OtpVerificationDto otpVerificationDto) {
        try {
            Optional<User> findUser = userRepository.findByEmail(otpVerificationDto.getEmail());
            if (findUser.isEmpty()) {
                throw new ResourceNotFoundException("User not found withd email", "email", otpVerificationDto.getEmail());
            }
            User user = findUser.get();

            // Kiểm tra type hợp lệ
            VerificationType verificationType;
            try {
                verificationType = VerificationType.valueOf(otpVerificationDto.getType());
            } catch (IllegalArgumentException e) {
                return ApiResponse.error("Type not found", HttpStatus.BAD_REQUEST, null);
            }

            // Lấy mã OTP
            VerificationCode verificationCode = verificationCodeRepository.findByUserAndType(user, verificationType).orElseThrow(
                    () -> new ResourceNotFoundException("OTP not found: ", "OTP", otpVerificationDto.getOtp()));

            // Kiểm tra OTP hết hạn
            if (verificationCode.getExpirationTime().isBefore(Instant.now())) {
                return ApiResponse.error("OTP has expired, please request new OTP", HttpStatus.BAD_REQUEST, null);
            }

            // Kiểm tra số lần thử
            if (verificationCode.getAttempts() >= 5) {
                return ApiResponse.error("Too many OTP attempts", HttpStatus.BAD_REQUEST, null);
            }

            // Xác minh OTP
            if (!passwordEncoder.matches(otpVerificationDto.getOtp(), verificationCode.getHashedCode())) {
                verificationCode.setAttempts(verificationCode.getAttempts() + 1);
                verificationCodeRepository.save(verificationCode);
                return ApiResponse.error("Invalid OTP", HttpStatus.BAD_REQUEST, null);
            }

            // Đánh dấu OTP đã được xác minh
            verificationCode.setIsVerified(true);
            verificationCodeRepository.save(verificationCode);

            // Xử lý theo loại OTP
            if (VerificationType.OTP_REGISTER.equals(verificationType)) {
                user.setIsVerified(true);
                userRepository.save(user);
                verificationCodeRepository.delete(verificationCode);
            } else if (VerificationType.OTP_FORGOT_PASSWORD.equals(verificationType)) {
                // Không xóa VerificationCode ngay, để resetPassword kiểm tra

            } else {
                return ApiResponse.error("OTP is not supported", HttpStatus.BAD_REQUEST, null);
            }

            return ApiResponse.success("OTP verification successful", HttpStatus.OK,
                    OtpVerificationResponse.builder().message("OTP verification successful").build());
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }


    public ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        try {
            Optional<User> findUser = userRepository.findByEmail(forgotPasswordDto.getEmail());
            if (findUser.isEmpty()) {
                throw new ResourceNotFoundException("User not found with email", "email", forgotPasswordDto.getEmail());
            }

            // Kiểm tra type hợp lệ
            VerificationType verificationType = VerificationType.OTP_FORGOT_PASSWORD;
            try {
                VerificationType.valueOf(verificationType.name());
            } catch (IllegalArgumentException e) {
                return ApiResponse.error("Type not found", HttpStatus.BAD_REQUEST, null);
            }


            return createOrUpdateOtp(findUser.get(), verificationType, ForgotPasswordResponse.builder().message("OTP has been sent to your email").build());
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    public ApiResponse<ResendOtpResponse> resendOtp(ResendOtpDto resendOtpDto) {
        try {
            Optional<User> findUser = userRepository.findByEmail(resendOtpDto.getEmail());

            if (findUser.isEmpty()) {
                throw new ResourceNotFoundException("User not found with email", "email", resendOtpDto.getEmail());
            }

            // Kiểm tra type hợp lệ
            VerificationType verificationType;
            try {
                verificationType = VerificationType.valueOf(resendOtpDto.getType());
            } catch (IllegalArgumentException e) {
                return ApiResponse.error("Type not found", HttpStatus.BAD_REQUEST, null);
            }

            // Nếu là OTP_REGISTER, kiểm tra chưa xác minh
            if (VerificationType.OTP_REGISTER.equals(verificationType) && findUser.get().getIsVerified()) {
                return ApiResponse.error("Verified account", HttpStatus.BAD_REQUEST, null);
            }

            return createOrUpdateOtp(findUser.get(), verificationType, ResendOtpResponse.builder().message("OTP has been sent to your email").build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<ResetPasswordResponse> resetPassword(ResetPasswordDto resetPasswordDto) {
        try {
            Optional<User> findUser = userRepository.findByEmail(resetPasswordDto.getEmail());
            if (findUser.isEmpty()) {
                throw new ResourceNotFoundException("User not found with email", "email", resetPasswordDto.getEmail());
            }
            User user = findUser.get();


            // Kiểm tra trạng thái xác minh OTP
            VerificationCode verificationCode = verificationCodeRepository.findByUserAndType(user, VerificationType.OTP_FORGOT_PASSWORD)
                    .orElseThrow(() -> new RuntimeException("OTP verification not available to reset password"));


            if(!verificationCode.getIsVerified()) {
                return ApiResponse.error("OTP not verified", HttpStatus.BAD_REQUEST, null);
            }

            // Kiểm tra OTP còn hiệu lực
            if (verificationCode.getExpirationTime().isBefore(Instant.now())) {
                return ApiResponse.error("OTP has expired, please request new OTP", HttpStatus.BAD_REQUEST, null);
            }

            // Cập nhật mật khẩu
            if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())) {
                return ApiResponse.error("The password and confirmation password do not match.", HttpStatus.BAD_REQUEST, null);
            }

            user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            user.setUpdatedBy(user.getEmail());
            userRepository.save(user);
            verificationCodeRepository.delete(verificationCode);

            return ApiResponse.success("Password reset successful", HttpStatus.OK, ResetPasswordResponse.builder()
                    .message("Password reset successful").build());
        } catch (Exception e) {
            return ApiResponse.error("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }


    private <T> ApiResponse<T> createOrUpdateOtp(User user, VerificationType verificationType, T response) {
        // 1. Tạo mới mã OTP
        String otp = generateOtp();
        String hashedOtp = passwordEncoder.encode(otp);

        // 2. Kiểm tra xem đã có VerificationCode chưa
        Optional<VerificationCode> existingCode = verificationCodeRepository.findByUserAndType(user, verificationType);
        VerificationCode verificationCode;
        if (existingCode.isPresent()) {
            // Cập nhật mã OTP
            verificationCode = existingCode.get();
            verificationCode.setHashedCode(hashedOtp);
            verificationCode.setExpirationTime(Instant.now().plusSeconds(OTP_EXPIRATION_SECONDS));
            verificationCode.setAttempts(0);
        } else {
            verificationCode = VerificationCode.builder()
                    .hashedCode(hashedOtp)
                    .expirationTime(Instant.now().plusSeconds(300))
                    .attempts(0)
                    .user(user)
                    .type(verificationType)
                    .isVerified(false)
                    .build();
        }
        verificationCodeRepository.save(verificationCode);

        emailService.sendOtpEmail(user.getEmail(), otp, verificationType);

        String message = VerificationType.OTP_REGISTER.equals(verificationType)
                ? "Registration successful, please check your email for OTP"
                : "OTP has been sent to your email";
        System.out.println(verificationType.name());

        HttpStatus httpStatus = VerificationType.OTP_REGISTER.equals(verificationType) ? HttpStatus.CREATED : HttpStatus.OK;

        return ApiResponse.success(message, httpStatus, response);

    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1000000));
    }

}
