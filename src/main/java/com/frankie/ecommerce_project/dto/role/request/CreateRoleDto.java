package com.frankie.ecommerce_project.dto.role.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRoleDto {
    private String id;
    private String name;
}
