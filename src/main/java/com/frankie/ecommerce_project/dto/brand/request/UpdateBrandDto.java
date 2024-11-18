package com.frankie.ecommerce_project.dto.brand.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBrandDto {
    private String id;
    private String name;
}
