package com.frankie.ecommerce_project.dto.brand.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBrandDto implements Serializable {
    private String id;
    private String name;
    private Boolean isDeleted;
}
