package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.product.common.ProductInfo;
import com.frankie.ecommerce_project.dto.product.request.CreateProductDto;
import com.frankie.ecommerce_project.dto.product.request.UpdateProductDto;
import com.frankie.ecommerce_project.dto.product.response.*;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.ProductMapper;
import com.frankie.ecommerce_project.model.Category;
import com.frankie.ecommerce_project.model.Product;
import com.frankie.ecommerce_project.repository.CategoryRepository;
import com.frankie.ecommerce_project.repository.ProductRepository;
import com.frankie.ecommerce_project.service.ProductService;
import com.frankie.ecommerce_project.utils.BuildPageable;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ApiResponse<CreateProductResponse> createProduct(CreateProductDto createProductDto) {
        try {
            Category findCategory = categoryRepository.findById(createProductDto.getCategory().getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Category", "id", createProductDto.getId()));
            Product newProduct = Product.builder()
                    .id(createProductDto.getId())
                    .name(createProductDto.getName())
                    .originalPrice(createProductDto.getOriginalPrice())
                    .discountPrice(createProductDto.getDiscountPrice())
                    .description(createProductDto.getDescription())
                    .quantity(createProductDto.getQuantity())
                    .category(findCategory)
                    .isActive(true)
                    .isDeleted(false)
                    .build();
            productRepository.save(newProduct);
            CreateProductResponse createCategoryResponse = ProductMapper.INSTACE.toCreateProductResponse(newProduct);
            return ApiResponse.success("Product created successfully", HttpStatus.CREATED, createCategoryResponse);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public ApiResponse<ProductListReponse> getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductInfo> productInfoList = buildProductInfoList(products);
        ProductListReponse productListResponse = buildProductListResponse(products, productInfoList);
        return ApiResponse.success("Get all products successfully", HttpStatus.OK, productListResponse);
    }

    @Override
    public ApiResponse<ProductInfo> getProductById(String id) {
        Product findProduct = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id));
        ProductInfo productInfo = ProductMapper.INSTACE.toProductInfo(findProduct);
        return ApiResponse.success("Get product by id successfully", HttpStatus.OK, productInfo);
    }

    @Override
    public ApiResponse<UpdateProductResponse> updateProductById(String id, UpdateProductDto updateProductDto) {
        Product findProduct = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id));
        Category findCategory = categoryRepository.findById(updateProductDto.getCategory().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Category", "id", updateProductDto.getCategory().getId()));
        findProduct.setName(updateProductDto.getName());
        findProduct.setOriginalPrice(updateProductDto.getOriginalPrice());
        findProduct.setDiscountPrice(updateProductDto.getDiscountPrice());
        findProduct.setDescription(updateProductDto.getDescription());
        findProduct.setQuantity(updateProductDto.getQuantity());
        findProduct.setCategory(findCategory);
        productRepository.save(findProduct);
        UpdateProductResponse updateProductResponse = ProductMapper.INSTACE.toUpdateProductResponse(findProduct);
        return ApiResponse.success("Update product by id successfully", HttpStatus.OK, updateProductResponse);
    }

    @Override
    public ApiResponse<DeleteProductResponse> softDeleteProductById(String id) {
        Product findProduct = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id));
        findProduct.setIsDeleted(true);
        findProduct.setIsActive(false);
        productRepository.save(findProduct);
        DeleteProductResponse deleteProductResponse = DeleteProductResponse.builder()
                .id(findProduct.getId())
                .isDeleted(findProduct.getIsDeleted())
                .isActive(findProduct.getIsActive())
                .updatedBy(findProduct.getUpdatedBy())
                .updatedAt(findProduct.getUpdatedAt())
                .build();
        return ApiResponse.success("Soft delete product by id successfully", HttpStatus.OK, deleteProductResponse);
    }

    @Override
    public ApiResponse<ReactivateProductResponse> reactivateProductById(String id) {
        Product findProduct = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id));
        if (findProduct.getIsActive() && !findProduct.getIsDeleted()) {
            return ApiResponse.error("Product is already active", HttpStatus.BAD_REQUEST, null);
        }
        findProduct.setIsActive(true);
        findProduct.setIsDeleted(false);
        productRepository.save(findProduct);
        ReactivateProductResponse reactivateProductResponse = ReactivateProductResponse.builder()
                .id(findProduct.getId())
                .isDeleted(findProduct.getIsDeleted())
                .isActive(findProduct.getIsActive())
                .updatedAt(findProduct.getUpdatedAt())
                .updatedBy(findProduct.getUpdatedBy())
                .build();
        return ApiResponse.success("Reactivate product by id successfully", HttpStatus.OK, reactivateProductResponse);
    }

    @Override
    public ApiResponse<ProductListReponse> searchProductByName(String name, int pageNo, int pageSize,
                                                               String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Product> products = productRepository.searchProductByName(name, pageable);
        List<ProductInfo> productInfoList = buildProductInfoList(products);
        ProductListReponse productListResponse = buildProductListResponse(products, productInfoList);
        return ApiResponse.success("Search product by name successfully", HttpStatus.OK, productListResponse);
    }

    private ProductListReponse buildProductListResponse(Page<Product> products, List<ProductInfo> productInfoList) {
        MetaData metaData = MetaData.builder()
                .pageNo(products.getNumber())
                .pageSize(products.getSize())
                .totalPages(products.getTotalPages())
                .totalElements(products.getTotalElements())
                .lastPage(products.isLast())
                .build();
        return ProductListReponse.builder().meta(metaData).data(productInfoList).build();
    }

    private List<ProductInfo> buildProductInfoList(Page<Product> products) {
        return products.getContent().stream()
                .map(ProductMapper.INSTACE::toProductInfo)
                .collect(Collectors.toList());
    }
}
