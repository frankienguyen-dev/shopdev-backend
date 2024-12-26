package com.frankie.ecommerce_project.dto.category.request;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCategoryDto implements Serializable {

    private String id;

    private String name;

    private String description;
}
