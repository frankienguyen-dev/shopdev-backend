package com.frankie.ecommerce_project.dto.permission.common;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionName implements Serializable {
    private String name;
}
