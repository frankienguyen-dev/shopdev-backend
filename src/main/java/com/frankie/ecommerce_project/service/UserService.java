package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.response.CreateUserResponse;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface UserService {
    ApiResponse<CreateUserResponse> createNewUser(CreateUserDto createUserDto);
}
