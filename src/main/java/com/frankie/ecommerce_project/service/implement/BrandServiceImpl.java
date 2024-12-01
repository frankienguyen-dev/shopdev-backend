package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.brand.common.BrandInfo;
import com.frankie.ecommerce_project.dto.brand.request.CreateBrandDto;
import com.frankie.ecommerce_project.dto.brand.request.UpdateBrandDto;
import com.frankie.ecommerce_project.dto.brand.response.*;
import com.frankie.ecommerce_project.exception.ResourceExistingException;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.BrandMapper;
import com.frankie.ecommerce_project.model.Brand;
import com.frankie.ecommerce_project.repository.BrandRepository;
import com.frankie.ecommerce_project.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public ApiResponse<CreateBrandResponse> createBrand(CreateBrandDto createBrandDto) {
        Optional<Brand> existingBrand = brandRepository.findByName(createBrandDto.getName());
        if (existingBrand.isPresent()) {
            return ApiResponse.error("Brand already exists", HttpStatus.BAD_REQUEST, null);
        }
        Brand newBrand = Brand.builder()
                .id(createBrandDto.getId())
                .name(createBrandDto.getName())
                .isDeleted(false)
                .build();
        brandRepository.save(newBrand);
        CreateBrandResponse createBrandResponse = BrandMapper.INSTANCE.toCreateBrandResponse(newBrand);
        return ApiResponse.success("Brand created successfully", HttpStatus.CREATED, createBrandResponse);
    }

    @Override
    public ApiResponse<BrandListResponse> getAllBrand(int pageNo, int pageSize, String sortBy,
                                                      String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Brand> brands = brandRepository.findAll(pageable);
        List<BrandInfo> brandInfoList = buildBrandInfoList(brands);
        BrandListResponse brandListResponse = buildBrandListResponse(brands, brandInfoList);
        return ApiResponse.success("Get all brands successfully", HttpStatus.OK, brandListResponse);
    }

    @Override
    public ApiResponse<BrandInfo> getBrandById(String id) {
        Brand findBrand = brandRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Id", "id", id));
        BrandInfo brandInfo = BrandMapper.INSTANCE.toBrandInfo(findBrand);
        return ApiResponse.success("Get brand by id successfully", HttpStatus.OK, brandInfo);
    }

    @Override
    public ApiResponse<UpdateBrandResponse> updateBrandById(String id, UpdateBrandDto updateBrandDto) {
        Optional<Brand> existingBrand = brandRepository.findByName(updateBrandDto.getName());
        if (existingBrand.isPresent() && !existingBrand.get().getId().endsWith(id)) {
            throw new ResourceExistingException("Brand name", "name", updateBrandDto.getName());
        }
        Brand findBrand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Id", "id", id));
        findBrand.setName(updateBrandDto.getName());
        brandRepository.save(findBrand);
        UpdateBrandResponse updateBrandResponse = BrandMapper.INSTANCE.toUpdateBrandResponse(findBrand);
        return ApiResponse.success("Update brand successfully", HttpStatus.OK, updateBrandResponse);
    }

    @Override
    public ApiResponse<DeleteBrandResponse> softDeleteBrandById(String id) {
        Brand findBrand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        findBrand.setIsDeleted(true);
        brandRepository.save(findBrand);
        DeleteBrandResponse deleteBrandResponse = DeleteBrandResponse.builder()
                .id(findBrand.getId())
                .isDeleted(findBrand.getIsDeleted())
                .updatedAt(findBrand.getUpdatedAt())
                .updatedBy(findBrand.getUpdatedBy())
                .build();
        return ApiResponse.success("Soft delete brand successfully", HttpStatus.OK, deleteBrandResponse);
    }

    @Override
    public ApiResponse<ReactiveBrandResponse> reactiveBrandById(String id) {
        Brand findBrand = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
        if (!findBrand.getIsDeleted()) {
            return ApiResponse.error("Brand is not deleted", HttpStatus.BAD_REQUEST, null);
        }
        findBrand.setIsDeleted(false);
        brandRepository.save(findBrand);
        ReactiveBrandResponse reactiveBrandResponse = ReactiveBrandResponse.builder()
                .id(findBrand.getId())
                .updatedAt(findBrand.getUpdatedAt())
                .updatedBy(findBrand.getUpdatedBy())
                .isDeleted(findBrand.getIsDeleted())
                .build();
        return ApiResponse.success("Reactive brand successfully", HttpStatus.OK, reactiveBrandResponse);
    }

    @Override
    public ApiResponse<BrandListResponse> searchBrandByName(String name, int pageNo, int pageSize,
                                                            String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Brand> brands = brandRepository.searchBrandByName(name, pageable);
        List<BrandInfo> buildBrandInfoList = buildBrandInfoList(brands);
        BrandListResponse buildBrandSearchListResponse = buildBrandListResponse(brands, buildBrandInfoList);
        return ApiResponse.success("Search brand by name successfully", HttpStatus.OK, buildBrandSearchListResponse);
    }

    private BrandListResponse buildBrandListResponse(Page<Brand> brands, List<BrandInfo> brandInfoList) {
        MetaData metaData = MetaData.builder()
                .pageNo(brands.getNumber())
                .pageSize(brands.getSize())
                .totalPages(brands.getTotalPages())
                .totalElements(brands.getTotalElements())
                .lastPage(brands.isLast())
                .build();
        return BrandListResponse.builder().meta(metaData).data(brandInfoList).build();
    }

    private List<BrandInfo> buildBrandInfoList(Page<Brand> brands) {
        return brands.getContent()
                .stream()
                .map(BrandMapper.INSTANCE::toBrandInfo)
                .collect(Collectors.toList());
    }
}
