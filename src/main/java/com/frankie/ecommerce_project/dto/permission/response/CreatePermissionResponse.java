package com.frankie.ecommerce_project.dto.permission.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePermissionResponse implements Serializable {

    private String id;

    private String name;

    private String path;

    private String method;

    private String module;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    private String createdBy;

    private Boolean isDeleted;
}
