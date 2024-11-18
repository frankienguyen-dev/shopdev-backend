package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.brand.common.BrandInfo;
import com.frankie.ecommerce_project.dto.brand.request.CreateBrandDto;
import com.frankie.ecommerce_project.dto.brand.request.UpdateBrandDto;
import com.frankie.ecommerce_project.dto.brand.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface BrandService {
    ApiResponse<CreateBrandResponse> createBrand(CreateBrandDto createBrandDto);

    ApiResponse<BrandListResponse> getAllBrand(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<BrandInfo> getBrandById(String id);

    ApiResponse<UpdateBrandResponse> updateBrandById(String id, UpdateBrandDto updateBrandDto);

    ApiResponse<DeleteBrandResponse> softDeleteBrandById(String id);

    ApiResponse<ReactiveBrandResponse> reactiveBrandById(String id);

    ApiResponse<BrandListResponse> searchBrandByName(String name, int pageNo, int pageSize,
                                                     String sortBy, String sortDir);
}
