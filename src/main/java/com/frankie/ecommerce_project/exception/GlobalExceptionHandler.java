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
    public ResponseEntity<ApiResponse<ErrorDetails>> handleGlobalException(Exception exception, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(Instant.now())
                .message(exception.getMessage())
                .details(request.getDescription(false))
                .build();
        ApiResponse<ErrorDetails> apiResponse = ApiResponse.error("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR, errorDetails);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getMessage())
                .timestamp(Instant.now())
                .details(request.getDescription(false))
                .build();
        ApiResponse<ErrorDetails> apiResponse = ApiResponse.error("Resource not found", HttpStatus.NOT_FOUND, errorDetails);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(ResourceExistingException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleResourceExistingException(ResourceExistingException exception, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getMessage())
                .timestamp(Instant.now())
                .details(request.getDescription(false))
                .build();
        ApiResponse<ErrorDetails> apiResponse = ApiResponse.error("Resource already exists", HttpStatus.BAD_REQUEST, errorDetails
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleIllegalStateException(IllegalStateException exception, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getMessage())
                .timestamp(Instant.now())
                .details(request.getDescription(false))
                .build();
        ApiResponse<ErrorDetails> apiResponse = ApiResponse.error("Operation not permitted in current state", HttpStatus.BAD_REQUEST, errorDetails);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorDetails>> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getMessage())
                .timestamp(Instant.now())
                .details(request.getDescription(false))
                .build();
        ApiResponse<ErrorDetails> apiResponse = ApiResponse.error("Invalid input data", HttpStatus.BAD_REQUEST, errorDetails);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

}
