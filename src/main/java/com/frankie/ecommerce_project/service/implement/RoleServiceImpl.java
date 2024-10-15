package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.dto.role.request.CreateRoleDto;
import com.frankie.ecommerce_project.dto.role.request.UpdateRoleDto;
import com.frankie.ecommerce_project.dto.role.response.*;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.RoleMapper;
import com.frankie.ecommerce_project.model.Role;
import com.frankie.ecommerce_project.repository.RoleRepository;
import com.frankie.ecommerce_project.service.RoleService;
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
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public ApiResponse<CreateRoleResponse> createNewRole(CreateRoleResponse createRoleResponse) {
        Optional<Role> findRoleByName = roleRepository.findByName(createRoleResponse.getName());
        if (findRoleByName.isPresent()) {
            return ApiResponse.error("Role already exists", HttpStatus.BAD_REQUEST, null);
        }
        Role newRole = Role.builder()
                .id(createRoleResponse.getId())
                .name(createRoleResponse.getName())
                .isDeleted(false)
                .build();
        roleRepository.save(newRole);
        CreateRoleResponse roleDtoMapping = RoleMapper.INSTANCE.toCreateRoleResponse(newRole);
        return ApiResponse.success("Role created successfully", HttpStatus.CREATED, roleDtoMapping);
    }

    @Override
    public ApiResponse<RoleListResponse> getAllRoles(int pageNo, int pageSize, String sortBy,
                                                     String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Role> roles = roleRepository.findAll(pageable);
        List<RoleInfo> roleInfoList = buildRoleList(roles);
        RoleListResponse roleListResponses = buildRoleListResponse(roles, roleInfoList);
        return ApiResponse.success(
                "Get all roles successfully",
                HttpStatus.OK,
                roleListResponses
        );
    }

    @Override
    public ApiResponse<RoleInfo> getRoleById(String id) {
        Role findRole = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Id", "id", id));
        RoleInfo roleInfo = RoleMapper.INSTANCE.toRoleInfo(findRole);
        return ApiResponse.success(
                "Get role by id successfully",
                HttpStatus.OK,
                roleInfo
        );
    }

    @Override
    public ApiResponse<UpdateRoleResponse> updateRoleById(String id, UpdateRoleDto updateRoleDto) {
        Optional<Role> existingRole = roleRepository.findByName(updateRoleDto.getName());
        if (existingRole.isPresent() && !existingRole.get().getName().equalsIgnoreCase(updateRoleDto.getName())) {
            return ApiResponse.error("Role already exists", HttpStatus.BAD_REQUEST, null);
        }
        Role findRole = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id));
        findRole.setName(updateRoleDto.getName());
        roleRepository.save(findRole);
        UpdateRoleResponse roleResponse = RoleMapper.INSTANCE.toUpdateRoleResponse(findRole);
        return ApiResponse.success(
                "Update role successfully",
                HttpStatus.OK,
                roleResponse
        );
    }

    @Override
    public ApiResponse<DeleteRoleResponse> softDeleteRoleById(String id) {
        Role findRole = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id));
        findRole.setIsDeleted(true);
        roleRepository.save(findRole);
        DeleteRoleResponse deleteRoleResponse = DeleteRoleResponse.builder()
                .id(findRole.getId())
                .isDeleted(findRole.getIsDeleted())
                .updatedAt(findRole.getUpdatedAt())
                .updatedBy(findRole.getUpdatedBy())
                .build();
        return ApiResponse.success(
                "Soft delete role successfully",
                HttpStatus.OK,
                deleteRoleResponse
        );
    }

    @Override
    public ApiResponse<ReactivateRoleResponse> reactivateRoleById(String id) {
        Role findRole = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role", "id", id));
        if (!findRole.getIsDeleted()) {
            return ApiResponse.error("Role is not deleted", HttpStatus.BAD_REQUEST, null);
        }
        findRole.setIsDeleted(false);
        roleRepository.save(findRole);
        ReactivateRoleResponse reactivateRoleResponse = ReactivateRoleResponse.builder()
                .id(findRole.getId())
                .isDeleted(findRole.getIsDeleted())
                .updatedBy(findRole.getUpdatedBy())
                .updatedAt(findRole.getUpdatedAt())
                .build();
        return ApiResponse.success(
                "Reactivate role successfully",
                HttpStatus.OK,
                reactivateRoleResponse
        );
    }

    @Override
    public ApiResponse<RoleListResponse> searchRoleByName(String name, int pageNo, int pageSize,
                                                          String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Role> roles = roleRepository.searchRoleByName(name, pageable);
        List<RoleInfo> roleInfoList = buildRoleList(roles);
        RoleListResponse roleListResponse = buildRoleListResponse(roles, roleInfoList);
        return ApiResponse.success(
                "Search role by name successfully",
                HttpStatus.OK,
                roleListResponse
        );
    }

    private List<RoleInfo> buildRoleList(Page<Role> roles) {
        return roles.getContent().stream()
                .map(RoleMapper.INSTANCE::toRoleInfo)
                .collect(Collectors.toList());
    }

    private RoleListResponse buildRoleListResponse(Page<Role> roles, List<RoleInfo> roleInfoList) {
        MetaData metaData = MetaData.builder()
                .pageNo(roles.getNumber())
                .pageSize(roles.getSize())
                .totalPages(roles.getTotalPages())
                .totalElements(roles.getTotalElements())
                .lastPage(roles.isLast())
                .build();
        return RoleListResponse.builder().meta(metaData).data(roleInfoList).build();
    }
}
