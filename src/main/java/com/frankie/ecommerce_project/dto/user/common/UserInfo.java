package com.frankie.ecommerce_project.dto.user.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private LocalDate dateOfBirth;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
    private Boolean isDeleted;
    private Boolean isActive;
}
