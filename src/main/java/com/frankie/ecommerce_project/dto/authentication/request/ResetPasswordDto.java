package com.frankie.ecommerce_project.dto.authentication.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordDto {
    private String email;

    private String password;

    private String confirmPassword;
}
