package com.frankie.ecommerce_project.mapper;

import com.frankie.ecommerce_project.dto.category.common.CategoryInfo;
import com.frankie.ecommerce_project.dto.category.response.CreateCategoryResponse;
import com.frankie.ecommerce_project.dto.category.response.UpdateCategoryResponse;
import com.frankie.ecommerce_project.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CreateCategoryResponse toCreateCategoryResponse(Category category);

    CategoryInfo toCategoryInfo(Category category);

    UpdateCategoryResponse toUpdateCategoryResponse(Category category);
}
