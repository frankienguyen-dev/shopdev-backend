package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.request.UpdateUserDto;
import com.frankie.ecommerce_project.dto.user.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface UserService {
    ApiResponse<CreateUserResponse> createNewUser(CreateUserDto createUserDto);

    ApiResponse<UserListResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<UserInfo> getUserById(String userId);

    ApiResponse<UpdateUserResponse> updateUserById(String userId, UpdateUserDto updateUserDto);

    ApiResponse<DeleteUserResponse> softDeleteUserById(String userId);

    ApiResponse<UserListResponse> searchUserByEmail(int pageNo, int pageSize, String sortBy, String sortDir, String userEmail);

    ApiResponse<ReactivateUserAccount> reactivateUserAccount(String userId);
}
