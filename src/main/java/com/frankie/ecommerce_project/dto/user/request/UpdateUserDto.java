package com.frankie.ecommerce_project.dto.user.request;

import com.frankie.ecommerce_project.dto.role.common.RoleName;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<RoleName> roles;
    private String address;
    private String avatar;
    private LocalDate dateOfBirth;
}
