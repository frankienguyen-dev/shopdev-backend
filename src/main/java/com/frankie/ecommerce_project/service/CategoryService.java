package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import com.frankie.ecommerce_project.dto.category.request.CreateCategoryDto;
import com.frankie.ecommerce_project.dto.category.request.UpdateCategoryDto;
import com.frankie.ecommerce_project.dto.category.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface CategoryService {
    ApiResponse<CreateCategoryResponse> createNewCategory(CreateCategoryDto createCategoryDto);

    ApiResponse<CategoryListResponse> getALlCategories(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<CategoryInfo> getCategoryById(String id);

    ApiResponse<UpdateCategoryResponse> updateCategory(String id, UpdateCategoryDto updateCategoryDto);

    ApiResponse<DeleteCategoryResponse> softDeleteCategoryById(String id);

    ApiResponse<ReactivateCategoryResponse> reactivateCategoryById(String id);

    ApiResponse<CategoryListResponse> searchCategoryByName(String name, int pageNo, int pageSize,
                                                           String sortBy, String sortDir);
}
