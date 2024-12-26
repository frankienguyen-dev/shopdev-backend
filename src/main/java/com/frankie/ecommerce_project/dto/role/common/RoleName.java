package com.frankie.ecommerce_project.dto.role.common;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleName implements Serializable {
    private String name;
}
