package com.frankie.ecommerce_project.dto.role.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRoleResponse {
    private String id;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    private Boolean isDeleted;
}
