package com.frankie.ecommerce_project.security.token;

import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.model.User;
import com.frankie.ecommerce_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JwtTokenProvider {

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
    private static final String ROLE_IDS_CLAIM = "roleIds";

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    /**
     * Constructs JwtTokenProvider with required dependencies and configuration.
     *
     * @param jwtEncoder             JWT encoder for token creation
     * @param userRepository         Repository for user data access
     * @param accessTokenExpiration  Access token expiration time in minutes
     * @param refreshTokenExpiration Refresh token expiration time in days
     */
    public JwtTokenProvider(JwtEncoder jwtEncoder,
                            UserRepository userRepository,
                            @Value("${app.accessTokenExpiration}") long accessTokenExpiration,
                            @Value("${app.refreshTokenExpiration}") long refreshTokenExpiration) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Creates an access token (JWT) containing user authentication and role information.
     *
     * @param authentication User authentication details
     * @return Encoded access token as a string
     * @throws ResourceNotFoundException If the user's email is not found
     */
    public String createAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiration = Instant.now().plus(accessTokenExpiration, ChronoUnit.MINUTES);

        String email = authentication.getName();
        User user = userRepository.findByEmailWithRoles(email).orElseThrow(() -> new ResourceNotFoundException("Email ", "email", email));

        List<String> roleIds = user.getRoles().stream().map(role -> role.getId()).collect(Collectors.toList());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(expiration)
                .claim(ROLE_IDS_CLAIM, roleIds)
                .build();

        return encodeJwt(claims);
    }

    /**
     * Creates a refresh token (JWT) for renewing access tokens.
     *
     * @param email User's email address
     * @return Encoded refresh token as a string
     */
    public String createRefreshToken(String email) {
        Instant now = Instant.now();
        Instant expiration = Instant.now().plus(refreshTokenExpiration, ChronoUnit.DAYS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(email)
                .issuedAt(now)
                .expiresAt(expiration)
                .build();

        return encodeJwt(claims);
    }

    /**
     * Encodes a JWT with the provided claims and JWS header.
     *
     * @param claims JWT claims to encode
     * @return Encoded JWT as a string
     */
    private String encodeJwt(JwtClaimsSet claims) {
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
