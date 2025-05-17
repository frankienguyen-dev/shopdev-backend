package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.product.request.CreateProductDto;
import com.frankie.ecommerce_project.dto.product.response.CreateProductResponse;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface ProductService {

    ApiResponse<CreateProductResponse> createProduct(CreateProductDto product);

}
