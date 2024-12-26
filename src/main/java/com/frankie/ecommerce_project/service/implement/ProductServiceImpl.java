package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.product.request.CreateProductDto;
import com.frankie.ecommerce_project.dto.product.response.CreateProductResponse;
import com.frankie.ecommerce_project.repository.ProductRepository;
import com.frankie.ecommerce_project.service.ProductService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public ApiResponse<CreateProductResponse> createProduct(CreateProductDto product) {
        productRepository.save(null);
        return null;
    }
}
