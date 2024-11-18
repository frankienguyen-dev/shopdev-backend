package com.frankie.ecommerce_project.mapper;

import com.frankie.ecommerce_project.dto.brand.common.BrandInfo;
import com.frankie.ecommerce_project.dto.brand.response.CreateBrandResponse;
import com.frankie.ecommerce_project.dto.brand.response.UpdateBrandResponse;
import com.frankie.ecommerce_project.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BrandMapper {
    BrandMapper INSTANCE = Mappers.getMapper(BrandMapper.class);

    CreateBrandResponse toCreateBrandResponse(Brand brand);

    BrandInfo toBrandInfo(Brand brand);

    UpdateBrandResponse toUpdateBrandResponse(Brand brand);
}
