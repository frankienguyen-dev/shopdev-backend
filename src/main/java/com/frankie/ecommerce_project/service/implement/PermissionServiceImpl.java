package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.permission.common.PermissionInfo;
import com.frankie.ecommerce_project.dto.permission.request.CreatePermissionDto;
import com.frankie.ecommerce_project.dto.permission.request.UpdatePermissionDto;
import com.frankie.ecommerce_project.dto.permission.response.CreatePermissionResponse;
import com.frankie.ecommerce_project.dto.permission.response.DeletePermissionResponse;
import com.frankie.ecommerce_project.dto.permission.response.PermissionListResponse;
import com.frankie.ecommerce_project.dto.permission.response.UpdatePermissionResponse;
import com.frankie.ecommerce_project.exception.ResourceExistingException;
import com.frankie.ecommerce_project.mapper.PermissionMapper;
import com.frankie.ecommerce_project.model.Permission;
import com.frankie.ecommerce_project.model.Role;
import com.frankie.ecommerce_project.repository.PermissionRepository;
import com.frankie.ecommerce_project.repository.RoleRepository;
import com.frankie.ecommerce_project.service.PermissionService;
import com.frankie.ecommerce_project.utils.BuildPageable;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    private final RoleRepository roleRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public ApiResponse<CreatePermissionResponse> createPermission(CreatePermissionDto permission) {

        Optional<Permission> existingPermission = permissionRepository.findByName(permission.getName());

        if (existingPermission.isPresent())
            throw new ResourceExistingException("Permission name", "name", permission.getName());

        Permission newPermission = Permission.builder()
                .id(permission.getId())
                .name(permission.getName())
                .path(permission.getPath())
                .method(permission.getMethod())
                .module(permission.getModule())
                .isDeleted(false)
                .build();

        permissionRepository.save(newPermission);

        CreatePermissionResponse permissionResponse = PermissionMapper.INSTANCE.toCreatePermissionResponse(newPermission);

        return ApiResponse.success("Permission created successfully", HttpStatus.CREATED, permissionResponse);
    }

    @Override
    public ApiResponse<PermissionListResponse> getAllPermissions(int pageNo, int pageSize, String sortBy, String sortDir) {

        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);

        Page<Permission> permissions = permissionRepository.findAll(pageable);

        List<PermissionInfo> permissionInfoList = buildPermissionInfoList(permissions);

        PermissionListResponse permissionListResponse = buildPermissionListResponse(permissions, permissionInfoList);

        return ApiResponse.success("Get all permissions successfully", HttpStatus.OK, permissionListResponse);
    }

    @Override
    public ApiResponse<PermissionInfo> getPermissionById(String permissionId) {

        Permission findPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceExistingException("Permission id", "id", permissionId));

        PermissionInfo permissionReponse = PermissionMapper.INSTANCE.toPermissionInfo(findPermission);

        return ApiResponse.success("Get permission by id successfully", HttpStatus.OK, permissionReponse);
    }

    @Override
    public ApiResponse<UpdatePermissionResponse> updatePermissionById(String permissionId, UpdatePermissionDto permission) {

        Optional<Permission> existingPermission = permissionRepository.findByName(permission.getName());

        if (existingPermission.isPresent() && !existingPermission.get().getId().equals(permissionId))
            throw new ResourceExistingException("Permission name", "name", permission.getName());

        Permission findPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceExistingException("Permission id", "id", permissionId));

        findPermission.setName(permission.getName());
        findPermission.setPath(permission.getPath());
        findPermission.setMethod(permission.getMethod());
        findPermission.setModule(permission.getModule());


        permissionRepository.save(findPermission);

        UpdatePermissionResponse updatePermissionResponse = PermissionMapper.INSTANCE.toUpdatePermissionResponse(findPermission);

        return ApiResponse.success("Permission updated successfully", HttpStatus.OK, updatePermissionResponse);
    }


    @Override
    @Transactional
    public ApiResponse<DeletePermissionResponse> deletePermissionById(String permissionId) {

        Permission findPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceExistingException("Permission id", "id", permissionId));

        Set<Role> roles = findPermission.getRoles();

        for (Role role : roles) {
            role.getPermissions().remove(findPermission);
        }

        permissionRepository.delete(findPermission);

        DeletePermissionResponse deletePermissionResponse = DeletePermissionResponse.builder()
                .id(findPermission.getId())
                .build();

        return ApiResponse.success("Permission deleted successfully", HttpStatus.OK, deletePermissionResponse);
    }


    private PermissionListResponse buildPermissionListResponse(Page<Permission> permissions, List<PermissionInfo> permissionInfoList) {

        MetaData meta = MetaData.builder()
                .pageNo(permissions.getNumber())
                .pageSize(permissions.getSize())
                .totalPages(permissions.getTotalPages())
                .totalElements(permissions.getTotalElements())
                .lastPage(permissions.isLast())
                .build();

        return PermissionListResponse.builder().meta(meta).data(permissionInfoList).build();

    }

    private List<PermissionInfo> buildPermissionInfoList(Page<Permission> permissions) {
        return permissions.getContent().stream()
                .map(PermissionMapper.INSTANCE::toPermissionInfo)
                .collect(Collectors.toList());
    }


}
