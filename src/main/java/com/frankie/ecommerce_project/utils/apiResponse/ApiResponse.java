package com.frankie.ecommerce_project.utils.apiResponse;

import lombok.*;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int statusCode;
    private T data;

    public static <T> ApiResponse<T> success(String message, HttpStatus statusCode, T data) {
        return new ApiResponse<>(message, statusCode.value(), data);
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus statusCode, T data) {
        return new ApiResponse<>(message, statusCode.value(), data);
    }
}
