package com.frankie.ecommerce_project.dto.authentication.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto implements Serializable {

    private String fullName;

    private String email;

    private String password;

    private String confirmPassword;

}
