package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import com.frankie.ecommerce_project.dto.category.request.CreateCategoryDto;
import com.frankie.ecommerce_project.dto.category.request.UpdateCategoryDto;
import com.frankie.ecommerce_project.dto.category.response.*;
import com.frankie.ecommerce_project.exception.ResourceExistingException;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.CategoryMapper;
import com.frankie.ecommerce_project.model.Category;
import com.frankie.ecommerce_project.repository.CategoryRepository;
import com.frankie.ecommerce_project.service.CategoryService;
import com.frankie.ecommerce_project.utils.BuildPageable;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ApiResponse<CreateCategoryResponse> createNewCategory(CreateCategoryDto createCategoryDto) {
        Optional<Category> existingCategory = categoryRepository.findByName(createCategoryDto.getName());
        if (existingCategory.isPresent()) {
            return ApiResponse.error("Category already exists", HttpStatus.BAD_REQUEST, null);
        }
        Category newCategory = Category.builder()
                .id(createCategoryDto.getId())
                .name(createCategoryDto.getName())
                .description(createCategoryDto.getDescription())
                .isDeleted(false)
                .build();
        categoryRepository.save(newCategory);
        CreateCategoryResponse createCategoryResponse = CategoryMapper.INSTANCE.toCreateCategoryResponse(newCategory);
        return ApiResponse.success("Category created successfully", HttpStatus.CREATED, createCategoryResponse);
    }

    @Override
    public ApiResponse<CategoryListResponse> getALlCategories(int pageNo, int pageSize, String sortBy,
            String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<CategoryInfo> categoryInfoList = buildCategoryList(categories);
        CategoryListResponse categoryListResponse = buildCategoryListResponse(categories, categoryInfoList);
        return ApiResponse.success("Get all categories successfully", HttpStatus.OK, categoryListResponse);
    }

    @Override
    public ApiResponse<CategoryInfo> getCategoryById(String categoryId) {
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category id", "id", categoryId));
        CategoryInfo categoryInfo = CategoryMapper.INSTANCE.toCategoryInfo(findCategory);
        return ApiResponse.success("Get category by id successfully", HttpStatus.OK, categoryInfo);
    }

    @Override
    public ApiResponse<UpdateCategoryResponse> updateCategory(String categoryId, UpdateCategoryDto updateCategoryDto) {
        Optional<Category> existingCategory = categoryRepository.findByName(updateCategoryDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(categoryId)) {
            throw new ResourceExistingException("Category name", "name", updateCategoryDto.getName());
        }
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category id", "id", categoryId));
        findCategory.setName(updateCategoryDto.getName());
        findCategory.setDescription(updateCategoryDto.getDescription());
        categoryRepository.save(findCategory);
        UpdateCategoryResponse updateCategoryResponse = CategoryMapper.INSTANCE.toUpdateCategoryResponse(findCategory);
        return ApiResponse.success("Category updated successfully", HttpStatus.OK, updateCategoryResponse);
    }

    @Override
    public ApiResponse<DeleteCategoryResponse> softDeleteCategoryById(String categoryId) {
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category id", "id", categoryId));
        findCategory.setIsDeleted(true);
        categoryRepository.save(findCategory);
        DeleteCategoryResponse deleteCategoryResponse = DeleteCategoryResponse.builder()
                .id(findCategory.getId())
                .updatedAt(findCategory.getUpdatedAt())
                .updatedBy(findCategory.getUpdatedBy())
                .isDeleted(findCategory.getIsDeleted())
                .build();
        return ApiResponse.success("Category deleted successfully", HttpStatus.OK, deleteCategoryResponse);
    }

    @Override
    public ApiResponse<ReactivateCategoryResponse> reactivateCategoryById(String categoryId) {
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category id", "id", categoryId));
        if (!findCategory.getIsDeleted()) {
            return ApiResponse.error("Category is already active", HttpStatus.BAD_REQUEST, null);
        }
        findCategory.setIsDeleted(false);
        categoryRepository.save(findCategory);
        ReactivateCategoryResponse reactivateCategoryResponse = ReactivateCategoryResponse.builder()
                .id(findCategory.getId())
                .updatedAt(findCategory.getUpdatedAt())
                .updatedBy(findCategory.getUpdatedBy())
                .isDeleted(findCategory.getIsDeleted())
                .build();
        return ApiResponse.success("Category reactivated successfully", HttpStatus.OK, reactivateCategoryResponse);
    }

    @Override
    public ApiResponse<CategoryListResponse> searchCategoryByName(String categoryName, int pageNo, int pageSize,
            String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Category> categories = categoryRepository.searchByName(categoryName, pageable);
        List<CategoryInfo> categoryInfoList = buildCategoryList(categories);
        CategoryListResponse categoryListResponse = buildCategoryListResponse(categories, categoryInfoList);
        return ApiResponse.success("Search category by name successfully", HttpStatus.OK, categoryListResponse);
    }

    private CategoryListResponse buildCategoryListResponse(Page<Category> categories,
            List<CategoryInfo> categoryInfoList) {
        MetaData metaData = MetaData.builder()
                .pageNo(categories.getNumber())
                .pageSize(categories.getSize())
                .totalPages(categories.getTotalPages())
                .totalElements(categories.getTotalElements())
                .lastPage(categories.isLast())
                .build();
        return CategoryListResponse.builder().meta(metaData).data(categoryInfoList).build();
    }

    private List<CategoryInfo> buildCategoryList(Page<Category> categories) {
        return categories.getContent().stream()
                .map(CategoryMapper.INSTANCE::toCategoryInfo)
                .collect(Collectors.toList());
    }
}
