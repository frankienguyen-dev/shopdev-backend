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
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.createBrand(createBrandDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<BrandListResponse>> getAllBrands(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,required = false) String sortDir) {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.getAllBrand(pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandInfo>> getBrandById(@PathVariable("id") String brandId) {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.getBrandById(brandId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateBrandResponse>> updateBrandById(
            @PathVariable("id") String brandId, @RequestBody UpdateBrandDto updateBrandDto) {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.updateBrandById(brandId, updateBrandDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteBrandResponse>> softDeleteBrandById(@PathVariable("id") String brandId) {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.softDeleteBrandById(brandId));
    }

    @PatchMapping("/reactive/{id}")
    public ResponseEntity<ApiResponse<ReactiveBrandResponse>> reactiveBrandById(@PathVariable("id") String brandId) {
        ApiResponse<ReactiveBrandResponse> reactiveBrandById = brandService.reactiveBrandById(brandId);
        return ResponseEntity.status(HttpStatus.OK).body(reactiveBrandById);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<BrandListResponse>> searchBrandByName(
            @RequestParam(value = "name", required = false) String brandName,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,required = false) String sortDir) {
        return ResponseEntity.status(HttpStatus.OK).body(brandService.searchBrandByName(brandName, pageNo, pageSize, sortBy, sortDir));
    }
}
