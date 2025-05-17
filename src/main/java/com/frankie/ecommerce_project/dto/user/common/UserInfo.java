package com.frankie.ecommerce_project.dto.user.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.dto.role.common.RoleName;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo implements Serializable {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private LocalDate dateOfBirth;
    private Set<RoleName> roles = new HashSet<>();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
    private Boolean isDeleted;
    private Boolean isActive;
    private Boolean isVerified;
}
