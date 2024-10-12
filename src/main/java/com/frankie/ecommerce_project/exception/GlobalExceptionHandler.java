package com.frankie.ecommerce_project.exception;

import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import com.frankie.ecommerce_project.utils.apiResponse.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleGlobalException(
            Exception exception, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(Instant.now())
                .message(exception.getMessage())
                .details(request.getDescription(false))
                .build();
        ApiResponse<ErrorDetails> apiResponse = ApiResponse.error(
                "An error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                errorDetails
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleResourceNotFoundException(
            ResourceNotFoundException exception, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getMessage())
                .timestamp(Instant.now())
                .details(request.getDescription(false))
                .build();
        ApiResponse<ErrorDetails> apiResponse = ApiResponse.error(
                "Resource not found",
                HttpStatus.NOT_FOUND,
                errorDetails
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }
}
