package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.product.common.ProductInfo;
import com.frankie.ecommerce_project.dto.product.request.CreateProductDto;
import com.frankie.ecommerce_project.dto.product.request.UpdateProductDto;
import com.frankie.ecommerce_project.dto.product.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface ProductService {
    ApiResponse<CreateProductResponse> createProduct(CreateProductDto createProductDto);

    ApiResponse<ProductListReponse> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<ProductInfo> getProductById(String id);

    ApiResponse<UpdateProductResponse> updateProductById(String id, UpdateProductDto updateProductDto);

    ApiResponse<DeleteProductResponse> softDeleteProductById(String id);

    ApiResponse<ReactivateProductResponse> reactivateProductById(String id);

    ApiResponse<ProductListReponse> searchProductByName(String name, int pageNo, int pageSize, String sortBy, String sortDir);
}
