package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.brand.common.BrandInfo;
import com.frankie.ecommerce_project.dto.brand.request.CreateBrandDto;
import com.frankie.ecommerce_project.dto.brand.request.UpdateBrandDto;
import com.frankie.ecommerce_project.dto.brand.response.*;
import com.frankie.ecommerce_project.service.BrandService;
import com.frankie.ecommerce_project.utils.AppConstants;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateBrandResponse>> createBrand(@RequestBody CreateBrandDto createBrandDto) {
        ApiResponse<CreateBrandResponse> createBrand = brandService.createBrand(createBrandDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createBrand);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<BrandListResponse>> getAllBrands(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir
    ) {
        ApiResponse<BrandListResponse> getAllBrands = brandService.getAllBrand(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(getAllBrands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandInfo>> getBrandById(@PathVariable String id) {
        ApiResponse<BrandInfo> getBrandById = brandService.getBrandById(id);
        return ResponseEntity.status(HttpStatus.OK).body(getBrandById);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateBrandResponse>> updateBrandById(
            @PathVariable String id, @RequestBody UpdateBrandDto updateBrandDto) {
        ApiResponse<UpdateBrandResponse> updateBrandById = brandService.updateBrandById(id, updateBrandDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateBrandById);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteBrandResponse>> softDeleteBrandById(@PathVariable String id) {
        ApiResponse<DeleteBrandResponse> softDeleteBrandById = brandService.softDeleteBrandById(id);
        return ResponseEntity.status(HttpStatus.OK).body(softDeleteBrandById);
    }

    @PatchMapping("/reactive/{id}")
    public ResponseEntity<ApiResponse<ReactiveBrandResponse>> reactiveBrandById(@PathVariable String id) {
        ApiResponse<ReactiveBrandResponse> reactiveBrandById = brandService.reactiveBrandById(id);
        return ResponseEntity.status(HttpStatus.OK).body(reactiveBrandById);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<BrandListResponse>> searchBrandByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                    required = false) String sortDir
    ) {
        ApiResponse<BrandListResponse> searchBrandByName = brandService
                .searchBrandByName(name, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(searchBrandByName);
    }
}
