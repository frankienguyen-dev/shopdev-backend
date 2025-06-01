package com.frankie.ecommerce_project.dto.authentication.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDto {
    private String refreshToken;
}
