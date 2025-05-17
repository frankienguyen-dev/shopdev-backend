package com.frankie.ecommerce_project.dto.authentication.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {

    private String email;

    private String fullName;

}
