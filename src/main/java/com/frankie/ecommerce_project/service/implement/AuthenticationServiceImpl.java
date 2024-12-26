package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.authentication.request.LoginDto;
import com.frankie.ecommerce_project.dto.authentication.response.LoginResponse;
import com.frankie.ecommerce_project.model.User;
import com.frankie.ecommerce_project.repository.UserRepository;
import com.frankie.ecommerce_project.security.token.JwtTokenProvider;
import com.frankie.ecommerce_project.service.AuthenticationService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider securityUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<LoginResponse> login(LoginDto loginDto) {
        try {
            Optional<User> userLogin = userRepository.findByEmail(loginDto.getEmail());

            if (userLogin.isEmpty() || !passwordEncoder.matches(loginDto.getPassword(), userLogin.get().getPassword())) {
                return ApiResponse.error("Email or password is incorrect", HttpStatus.BAD_REQUEST, null);
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                    UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

            Authentication authentication = authenticationManagerBuilder
                    .getObject()
                    .authenticate(usernamePasswordAuthenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = securityUtils.createToken(authentication);

            LoginResponse loginResponse = new LoginResponse(accessToken);

            return ApiResponse.success(
                    "Login successfully",
                    HttpStatus.OK,
                    loginResponse
            );
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        }
    }

}
