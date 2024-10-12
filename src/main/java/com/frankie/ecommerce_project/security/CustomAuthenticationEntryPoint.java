package com.frankie.ecommerce_project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");
        String errorMessage = (authException.getCause() != null)
                ? authException.getCause().getMessage()
                : authException.getMessage();
        ApiResponse<Object> res = ApiResponse.error(
                "Token is missing or invalid",
                HttpStatus.UNAUTHORIZED,
                errorMessage
        );
        objectMapper.writeValue(response.getWriter(), res);
    }
}
