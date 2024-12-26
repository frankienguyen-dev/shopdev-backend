package com.frankie.ecommerce_project.dto.category.response;

import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryListResponse implements Serializable {
    private MetaData meta;
    private List<CategoryInfo> data;
}
