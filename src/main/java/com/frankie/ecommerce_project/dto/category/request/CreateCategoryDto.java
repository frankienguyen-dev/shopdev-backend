package com.frankie.ecommerce_project.dto.category.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCategoryDto implements Serializable {
    private String id;
    private String name;
    private String description;
    private Boolean isDeleted;
}
