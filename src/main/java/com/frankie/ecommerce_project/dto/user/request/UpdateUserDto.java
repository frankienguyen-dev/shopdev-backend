package com.frankie.ecommerce_project.dto.user.request;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private LocalDate dateOfBirth;
}
