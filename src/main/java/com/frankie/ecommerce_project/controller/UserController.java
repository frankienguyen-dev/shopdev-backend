package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.response.CreateUserResponse;
import com.frankie.ecommerce_project.service.UserService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(
            @RequestBody CreateUserDto createUserDto) {
        ApiResponse<CreateUserResponse> createUserResponse = userService.createNewUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserResponse);
    }
}
