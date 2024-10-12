package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.authentication.request.LoginDto;
import com.frankie.ecommerce_project.dto.authentication.response.LoginResponse;
import com.frankie.ecommerce_project.security.token.JwtTokenProvider;
import com.frankie.ecommerce_project.service.AuthenticationService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider securityUtils;

    @Override
    public ApiResponse<LoginResponse> login(LoginDto loginDto) {
       try {
           UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                   UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
           Authentication authentication = authenticationManagerBuilder
                   .getObject()
                   .authenticate(usernamePasswordAuthenticationToken);
           SecurityContextHolder.getContext().setAuthentication(authentication);
           String accessToken = securityUtils.createToken(authentication);
           LoginResponse loginResponse = new LoginResponse(accessToken);
           return ApiResponse.success(
                   "Login successful",
                   HttpStatus.OK,
                   loginResponse
           );
       } catch (Exception e) {
           e.printStackTrace();
           throw e;
       }
    }

}
