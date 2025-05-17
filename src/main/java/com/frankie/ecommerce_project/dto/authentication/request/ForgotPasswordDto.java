package com.frankie.ecommerce_project.dto.authentication.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgotPasswordDto {

    private String email;

}
