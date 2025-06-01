package com.frankie.ecommerce_project.dto.authentication.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
