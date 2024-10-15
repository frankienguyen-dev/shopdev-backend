package com.frankie.ecommerce_project.dto.role.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactivateRoleResponse {
    private String id;
    private Boolean isDeleted;
    private Instant updatedAt;
    private String updatedBy;
}
