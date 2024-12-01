package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.product.common.ProductInfo;
import com.frankie.ecommerce_project.dto.product.request.CreateProductDto;
import com.frankie.ecommerce_project.dto.product.request.UpdateProductDto;
import com.frankie.ecommerce_project.dto.product.response.*;
import com.frankie.ecommerce_project.service.ProductService;
import com.frankie.ecommerce_project.utils.AppConstants;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateProductResponse>> createProduct(@RequestBody CreateProductDto product) {
        ApiResponse<CreateProductResponse> createProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createProduct);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductListReponse>> getAllProducts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir) {
        ApiResponse<ProductListReponse> getAllProducts = productService.getAllProducts(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(getAllProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductInfo>> getProductById(@PathVariable String id) {
        ApiResponse<ProductInfo> getProductById = productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(getProductById);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateProductResponse>> updateProductById(
            @PathVariable String id, @RequestBody UpdateProductDto updateProductDto) {
        ApiResponse<UpdateProductResponse> updateProductById = productService.updateProductById(id, updateProductDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateProductById);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteProductResponse>> softDeleteProductById(@PathVariable String id) {
        ApiResponse<DeleteProductResponse> deletedProduct = productService.softDeleteProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedProduct);
    }

    @PatchMapping("/reactivate/{id}")
    public ResponseEntity<ApiResponse<ReactivateProductResponse>> reactivateProductById(@PathVariable String id) {
        ApiResponse<ReactivateProductResponse> reactivatedProduct = productService.reactivateProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(reactivatedProduct);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ProductListReponse>> searchProductByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir) {
        ApiResponse<ProductListReponse> searchProductByName = productService.searchProductByName(name, pageNo,
                pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(searchProductByName);
    }
}

