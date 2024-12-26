package com.frankie.ecommerce_project.dto.user.request;

import com.frankie.ecommerce_project.dto.role.common.RoleName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserDto implements Serializable {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private Set<RoleName> roles = new HashSet<>();
    private String address;
    private String avatar;
    private LocalDate dateOfBirth;
}
