package com.frankie.ecommerce_project.dto.role.response;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactivateRoleResponse implements Serializable {
    private String id;
    private Boolean isDeleted;
    private Instant updatedAt;
    private String updatedBy;
}
