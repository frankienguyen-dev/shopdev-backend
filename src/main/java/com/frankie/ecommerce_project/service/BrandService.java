package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.brand.common.BrandInfo;
import com.frankie.ecommerce_project.dto.brand.request.CreateBrandDto;
import com.frankie.ecommerce_project.dto.brand.request.UpdateBrandDto;
import com.frankie.ecommerce_project.dto.brand.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface BrandService {
    ApiResponse<CreateBrandResponse> createBrand(CreateBrandDto createBrandDto);

    ApiResponse<BrandListResponse> getAllBrand(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<BrandInfo> getBrandById(String brandId);

    ApiResponse<UpdateBrandResponse> updateBrandById(String brandId, UpdateBrandDto updateBrandDto);

    ApiResponse<DeleteBrandResponse> softDeleteBrandById(String brandId);

    ApiResponse<ReactiveBrandResponse> reactiveBrandById(String brandId);

    ApiResponse<BrandListResponse> searchBrandByName(String brandName, int pageNo, int pageSize,
                                                     String sortBy, String sortDir);
}
