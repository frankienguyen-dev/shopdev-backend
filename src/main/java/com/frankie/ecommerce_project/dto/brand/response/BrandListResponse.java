package com.frankie.ecommerce_project.dto.brand.response;

import com.frankie.ecommerce_project.dto.brand.common.BrandInfo;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandListResponse {
    private MetaData meta;
    private List<BrandInfo> data;
}
