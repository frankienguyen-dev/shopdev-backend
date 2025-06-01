package com.frankie.ecommerce_project.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecurityUtil {
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContextHolder.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) return null;
        else if (authentication.getPrincipal() instanceof UserDetails userDetails) return userDetails.getUsername();
        else if (authentication.getPrincipal() instanceof Jwt jwt) return jwt.getSubject();
        else if (authentication.getPrincipal() instanceof String str) return str;
        return null;
    }

//    public List<String> getRoleIdsFromToken() {
//
//    }
}
