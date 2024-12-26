package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.request.UpdateUserDto;
import com.frankie.ecommerce_project.dto.user.response.*;
import com.frankie.ecommerce_project.service.UserService;
import com.frankie.ecommerce_project.utils.AppConstants;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(
            @RequestBody CreateUserDto createUserDto) {
        ApiResponse<CreateUserResponse> createUserResponse = userService.createNewUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir) {
        ApiResponse<UserListResponse> getAllUser = userService.getAllUsers(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(getAllUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserInfo>> getUserInfoById(@PathVariable("id") String userId) {
        ApiResponse<UserInfo> getUser = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(getUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUserById(
            @PathVariable("id") String userId, @RequestBody UpdateUserDto updateUserDto) {
        ApiResponse<UpdateUserResponse> updateUser = userService.updateUserById(userId, updateUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> softDeleteUserById(@PathVariable("id") String userId) {
        ApiResponse<DeleteUserResponse> deleteUser = userService.softDeleteUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(deleteUser);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserListResponse>> searchUserByEmail(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir,
            @RequestParam(value = "email", required = false) String userEmail) {
        ApiResponse<UserListResponse> searchListUser = userService
                .searchUserByEmail(pageNo, pageSize, sortBy, sortDir, userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(searchListUser);
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<ReactivateUserAccount>> reactivateUserAccount(@PathVariable("id") String userId) {
        ApiResponse<ReactivateUserAccount> reactivateUser = userService.reactivateUserAccount(userId);
        return ResponseEntity.status(HttpStatus.OK).body(reactivateUser);
    }
}
