package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.request.UpdateUserDto;
import com.frankie.ecommerce_project.dto.user.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface UserService {
    ApiResponse<CreateUserResponse> createNewUser(CreateUserDto createUserDto);

    ApiResponse<UserListResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<UserInfo> getUserById(String id);

    ApiResponse<UpdateUserResponse> updateUserById(String id, UpdateUserDto updateUserDto);

    ApiResponse<DeleteUserResponse> softDeleteUserById(String id);

    ApiResponse<UserListResponse> searchUserByEmail(
            int pageNo, int pageSize, String sortBy, String sortDir, String email);

    ApiResponse<ReactivateUserAccount> reactivateUserAccount(String id);
}
