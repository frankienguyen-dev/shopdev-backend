package com.frankie.ecommerce_project.dto.user.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactivateUserAccount {
    private String id;
    private Boolean isDeleted;
    private Boolean isActive;
    private Instant updatedAt;
    private String updatedBy;
}
