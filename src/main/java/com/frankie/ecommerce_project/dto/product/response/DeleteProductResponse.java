package com.frankie.ecommerce_project.dto.product.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteProductResponse {
    private String id;
    private Boolean isActive;
    private Boolean isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
}
