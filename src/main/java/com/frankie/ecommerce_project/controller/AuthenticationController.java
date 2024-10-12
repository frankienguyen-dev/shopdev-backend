package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.authentication.request.LoginDto;
import com.frankie.ecommerce_project.dto.authentication.response.LoginResponse;
import com.frankie.ecommerce_project.service.AuthenticationService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginDto loginDto) {
        ApiResponse<LoginResponse> login = authenticationService.login(loginDto);
        return ResponseEntity.ok().body(login);
    }

}
