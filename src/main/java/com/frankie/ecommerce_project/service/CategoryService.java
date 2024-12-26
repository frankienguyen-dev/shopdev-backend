package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import com.frankie.ecommerce_project.dto.category.request.CreateCategoryDto;
import com.frankie.ecommerce_project.dto.category.request.UpdateCategoryDto;
import com.frankie.ecommerce_project.dto.category.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface CategoryService {
    ApiResponse<CreateCategoryResponse> createNewCategory(CreateCategoryDto createCategoryDto);

    ApiResponse<CategoryListResponse> getALlCategories(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<CategoryInfo> getCategoryById(String categoryId);

    ApiResponse<UpdateCategoryResponse> updateCategory(String categoryId, UpdateCategoryDto updateCategoryDto);

    ApiResponse<DeleteCategoryResponse> softDeleteCategoryById(String categoryId);

    ApiResponse<ReactivateCategoryResponse> reactivateCategoryById(String categoryId);

    ApiResponse<CategoryListResponse> searchCategoryByName(String categoryName, int pageNo, int pageSize,
                                                           String sortBy, String sortDir);
}
