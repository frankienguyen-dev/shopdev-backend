package com.frankie.ecommerce_project.dto.user.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserDto {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String avatar;
    private LocalDate dateOfBirth;
}
