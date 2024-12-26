package com.frankie.ecommerce_project.dto.brand.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBrandDto implements Serializable {
    private String id;
    private String name;
}
