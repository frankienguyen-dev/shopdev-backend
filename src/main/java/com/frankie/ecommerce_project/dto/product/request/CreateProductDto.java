package com.frankie.ecommerce_project.dto.product.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductDto {

    private String id;

    private String name;

    private Long originalPrice;

    private Long discountPrice;

    private int rating;

    private String description;

    private int quantity;

    private Boolean isActive;

    private Boolean isDeleted;
}
