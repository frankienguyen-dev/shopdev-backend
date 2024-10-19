package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import com.frankie.ecommerce_project.dto.category.request.CreateCategoryDto;
import com.frankie.ecommerce_project.dto.category.request.UpdateCategoryDto;
import com.frankie.ecommerce_project.dto.category.response.*;
import com.frankie.ecommerce_project.service.CategoryService;
import com.frankie.ecommerce_project.utils.AppConstants;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateCategoryResponse>> createNewCategory(
            @RequestBody CreateCategoryDto createCategoryDto) {
        ApiResponse<CreateCategoryResponse> createCategory = categoryService.createNewCategory(createCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createCategory);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CategoryListResponse>> getAllCategories(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir) {
        ApiResponse<CategoryListResponse> allCategories = categoryService.getALlCategories(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(allCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryInfo>> getCategoryById(@PathVariable String id) {
        ApiResponse<CategoryInfo> getCategoryById = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(getCategoryById);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateCategoryResponse>> updateCategoryById(
            @PathVariable String id, @RequestBody UpdateCategoryDto updateCategoryDto) {
        ApiResponse<UpdateCategoryResponse> updateCategory = categoryService.updateCategory(id, updateCategoryDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteCategoryResponse>> softDeleteCategoryById(@PathVariable String id) {
        ApiResponse<DeleteCategoryResponse> deleteCategory = categoryService.softDeleteCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleteCategory);
    }

    @PatchMapping("reactivate/{id}")
    public ResponseEntity<ApiResponse<ReactivateCategoryResponse>> reactivateCategoryById(@PathVariable String id) {
        ApiResponse<ReactivateCategoryResponse> reactivateCategory = categoryService.reactivateCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(reactivateCategory);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CategoryListResponse>> searchCategoryByName(
            @RequestParam(value = "name", required = false) String email,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir) {
        ApiResponse<CategoryListResponse> searchByName = categoryService.searchCategoryByName(
                email, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(searchByName);
    }
}
