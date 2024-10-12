package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.authentication.request.LoginDto;
import com.frankie.ecommerce_project.dto.authentication.response.LoginResponse;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface AuthenticationService {
    ApiResponse<LoginResponse> login(LoginDto loginDto);
}
