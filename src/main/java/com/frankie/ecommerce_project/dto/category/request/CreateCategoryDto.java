package com.frankie.ecommerce_project.dto.category.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCategoryDto {
    private String id;
    private String name;
    private String description;
    private Boolean isDeleted;
}
