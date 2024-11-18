package com.frankie.ecommerce_project.dto.brand.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBrandDto {
    private String id;
    private String name;
    private Boolean isDeleted;
}
