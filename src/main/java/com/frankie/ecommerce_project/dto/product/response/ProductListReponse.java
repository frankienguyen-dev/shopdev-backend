package com.frankie.ecommerce_project.dto.product.response;

import com.frankie.ecommerce_project.dto.product.common.ProductInfo;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListReponse {
    private MetaData meta;
    private List<ProductInfo> data;
}
