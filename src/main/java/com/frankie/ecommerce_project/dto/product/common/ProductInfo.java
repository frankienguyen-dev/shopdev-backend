package com.frankie.ecommerce_project.dto.product.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import com.frankie.ecommerce_project.model.Category;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInfo {
    private String id;
    private String name;
    private Long originalPrice;
    private Long discountPrice;
    private String description;
    private int quantity;
    private CategoryInfo category;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;
    private Boolean isActive;
    private Boolean isDeleted;

}
