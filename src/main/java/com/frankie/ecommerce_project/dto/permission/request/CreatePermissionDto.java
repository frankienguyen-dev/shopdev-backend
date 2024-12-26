package com.frankie.ecommerce_project.dto.permission.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePermissionDto implements Serializable {

    private String id;

    private String name;

    private String path;

    private String method;

    private String module;

    private Boolean isDeleted;

}
