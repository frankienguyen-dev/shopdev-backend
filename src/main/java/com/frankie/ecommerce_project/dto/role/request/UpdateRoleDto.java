package com.frankie.ecommerce_project.dto.role.request;

import com.frankie.ecommerce_project.dto.permission.common.PermissionName;
import com.frankie.ecommerce_project.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleDto implements Serializable {
    private String id;
    private String name;
    private Set<PermissionName> permissions = new HashSet<>();
}
