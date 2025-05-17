package com.frankie.ecommerce_project.dto.authentication.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResendOtpDto {

    private String email;

    private String type;

}
