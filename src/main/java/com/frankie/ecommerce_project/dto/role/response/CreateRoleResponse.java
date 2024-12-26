package com.frankie.ecommerce_project.dto.role.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.dto.permission.common.PermissionName;
import com.frankie.ecommerce_project.model.Permission;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRoleResponse implements Serializable {
    private String id;
    private String name;
    private Set<PermissionName> permissions = new HashSet<>();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    private Boolean isDeleted;
}
