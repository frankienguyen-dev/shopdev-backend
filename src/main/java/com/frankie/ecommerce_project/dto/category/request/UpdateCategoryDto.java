package com.frankie.ecommerce_project.dto.category.request;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategoryDto {
    private String id;
    private String name;
    private String description;
}
