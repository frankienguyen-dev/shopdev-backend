package com.frankie.ecommerce_project.mapper;

import com.frankie.ecommerce_project.dto.product.response.CreateProductResponse;
import com.frankie.ecommerce_project.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    CreateProductResponse toCreateProductResponse(Product product);
}
