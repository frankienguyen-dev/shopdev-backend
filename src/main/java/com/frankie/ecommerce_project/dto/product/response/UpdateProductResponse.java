package com.frankie.ecommerce_project.dto.product.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductResponse {
    private String id;
    private String name;
    private Long originalPrice;
    private Long discountPrice;
    private String description;
    private int quantity;
    private CategoryInfo category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
}
