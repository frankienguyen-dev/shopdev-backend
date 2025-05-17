package com.frankie.ecommerce_project.dto.authentication.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpVerificationDto {

    private String email;

    private String otp;

    private String type;
}
