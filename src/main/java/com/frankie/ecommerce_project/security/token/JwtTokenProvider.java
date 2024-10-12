package com.frankie.ecommerce_project.security.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtTokenProvider {
    private final JwtEncoder jwtEncoder;
    @Value("${app.accessTokenSecret}")
    private String jwtSecret;
    @Value("${app.accessTokenExpiration}")
    private int exp;

    public JwtTokenProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public String createToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiration = Instant.now().plus(exp, ChronoUnit.MINUTES);

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("authorities", authentication.getAuthorities())
                .build();
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }


}
