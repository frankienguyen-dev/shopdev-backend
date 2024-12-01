package com.frankie.ecommerce_project.dto.product.request;

import com.frankie.ecommerce_project.dto.category.common.CategoryId;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductDto {
    private String id;
    private String name;
    private Long originalPrice;
    private Long discountPrice;
    private String description;
    private int quantity;
    private CategoryId category;
}
