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
import com.frankie.ecommerce_project.repository.PermissionRepository;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of PermissionService for managing permission-related operations.
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    private static final String SUCCESS_MESSAGE_CREATE = "Permission created successfully";
    private static final String SUCCESS_MESSAGE_GET_ALL = "Get all permissions successfully";
    private static final String SUCCESS_MESSAGE_GET_BY_ID = "Get permission by id successfully";
    private static final String SUCCESS_MESSAGE_UPDATE = "Permission updated successfully";
    private static final String SUCCESS_MESSAGE_DELETE = "Permission deleted successfully";

    private final PermissionRepository permissionRepository;

    /**
     * Constructs a new PermissionServiceImpl with the specified repositories.
     *
     * @param permissionRepository The repository for accessing permission data.
     */
    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Creates a new permission with the provided details.
     *
     * @param createPermissionDto Permission creation details
     * @return ApiResponse containing the created permission's information
     * @throws ResourceExistingException if the permission name already exists
     */
    @Transactional
    @Override
    public ApiResponse<CreatePermissionResponse> createPermission(CreatePermissionDto createPermissionDto) {
        checkPermissionExists(createPermissionDto.getName(), null);
        Permission newPermission = createNewPermission(createPermissionDto);
        permissionRepository.save(newPermission);
        CreatePermissionResponse response = PermissionMapper.INSTANCE.toCreatePermissionResponse(newPermission);
        return ApiResponse.success(SUCCESS_MESSAGE_CREATE, HttpStatus.CREATED, response);
    }

    /**
     * Retrieves a paginated list of all permissions.
     *
     * @param pageNo   The page number to retrieve (zero-based).
     * @param pageSize The number of permissions per page.
     * @param sortBy   The field to sort by.
     * @param sortDir  The sort direction ("asc" or "desc").
     * @return ApiResponse containing the paginated list of permissions.
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<PermissionListResponse> getAllPermissions(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        return buildPermissionListResponse(permissions);
    }

    /**
     * Retrieves a permission by its ID.
     *
     * @param permissionId The ID of the permission to retrieve.
     * @return ApiResponse containing the permission details.
     * @throws ResourceExistingException if the permission ID does not exist.
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<PermissionInfo> getPermissionById(String permissionId) {
        Permission permission = findPermissionById(permissionId);
        PermissionInfo response = PermissionMapper.INSTANCE.toPermissionInfo(permission);
        return ApiResponse.success(SUCCESS_MESSAGE_GET_BY_ID, HttpStatus.OK, response);
    }

    /**
     * Updates an existing permission with the provided details.
     *
     * @param permissionId The ID of the permission to update.
     * @param updatePermissionDto   The DTO containing updated permission details.
     * @return ApiResponse containing the updated permission's information.
     * @throws ResourceExistingException if the permission ID does not exist or the new name already exists.
     */
    @Transactional
    @Override
    public ApiResponse<UpdatePermissionResponse> updatePermissionById(String permissionId, UpdatePermissionDto updatePermissionDto) {
        checkPermissionExists(updatePermissionDto.getName(), permissionId);
        Permission permission = findPermissionById(permissionId);
        updatePermission(permission, updatePermissionDto);
        permissionRepository.save(permission);
        UpdatePermissionResponse response = PermissionMapper.INSTANCE.toUpdatePermissionResponse(permission);
        return ApiResponse.success(SUCCESS_MESSAGE_UPDATE, HttpStatus.OK, response);
    }

    /**
     * Deletes a permission by its ID and removes it from associated roles.
     *
     * @param permissionId The ID of the permission to delete.
     * @return ApiResponse containing the deletion confirmation.
     * @throws ResourceExistingException if the permission ID does not exist.
     */
    @Transactional
    @Override
    public ApiResponse<DeletePermissionResponse> deletePermissionById(String permissionId) {
        Permission permission = findPermissionById(permissionId);
        removePermissionFromRoles(permission);
        permissionRepository.delete(permission);
        DeletePermissionResponse response = DeletePermissionResponse.builder()
                .id(permission.getId())
                .build();
        return ApiResponse.success(SUCCESS_MESSAGE_DELETE, HttpStatus.OK, response);
    }

    /**
     * Builds an ApiResponse containing a paginated list of permissions and associated metadata.
     *
     * @param permissions Page of Permission entities retrieved from the database
     * @return ApiResponse object containing PermissionListResponse data and pagination metadata
     */
    private ApiResponse<PermissionListResponse> buildPermissionListResponse(Page<Permission> permissions) {
        List<PermissionInfo> permissionInfoList = mapToPermissionInfoList(permissions);
        MetaData meta = MetaData.builder()
                .pageNo(permissions.getNumber())
                .pageSize(permissions.getSize())
                .totalPages(permissions.getTotalPages())
                .totalElements(permissions.getTotalElements())
                .lastPage(permissions.isLast())
                .build();
        PermissionListResponse response = PermissionListResponse.builder()
                .meta(meta)
                .data(permissionInfoList)
                .build();
        return ApiResponse.success(SUCCESS_MESSAGE_GET_ALL, HttpStatus.OK, response);
    }

    /**
     * Maps a page of permissions to a list of PermissionInfo DTOs.
     *
     * @param permissions The paginated list of permissions.
     * @return A list of PermissionInfo DTOs.
     */
    private List<PermissionInfo> mapToPermissionInfoList(Page<Permission> permissions) {
        return permissions.getContent().stream()
                .map(PermissionMapper.INSTANCE::toPermissionInfo)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a permission name already exists, excluding the permission with the given ID (if any).
     *
     * @param permissionName Permission name to check
     * @param permissionId   ID of the permission to exclude (null for creation, non-null for update)
     * @throws ResourceExistingException if the permission name already exists
     */
    private void checkPermissionExists(String permissionName, String permissionId) {
        permissionRepository.findByName(permissionName)
                .filter(permission -> permissionId == null || !Objects.equals(permission.getId(), permissionId))
                .ifPresent(permission -> {
                    throw new ResourceExistingException("Permission", "name", permissionName);
                });
    }

    /**
     * Finds a permission by its ID or throws an exception if not found.
     *
     * @param permissionId The ID of the permission to find.
     * @return The found Permission entity.
     * @throws ResourceExistingException if the permission ID does not exist.
     */
    private Permission findPermissionById(String permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceExistingException("Permission", "id", permissionId));
    }

    /**
     * Builds a new Permission entity from the provided CreatePermissionDto.
     *
     * @param createPermissionDto DTO containing permission creation details
     * @return Permission entity with initialized fields
     */
    private Permission createNewPermission(CreatePermissionDto createPermissionDto) {
        return Permission.builder()
                .id(createPermissionDto.getId())
                .name(createPermissionDto.getName())
                .path(createPermissionDto.getPath())
                .method(createPermissionDto.getMethod())
                .module(createPermissionDto.getModule())
                .isDeleted(false)
                .build();
    }

    /**
     * Updates the details of an existing permission with the provided DTO.
     *
     * @param permission The Permission entity to update.
     * @param updatePermissionDto        The DTO containing updated permission details.
     */
    private void updatePermission(Permission permission, UpdatePermissionDto updatePermissionDto) {
        permission.setName(updatePermissionDto.getName());
        permission.setPath(updatePermissionDto.getPath());
        permission.setMethod(updatePermissionDto.getMethod());
        permission.setModule(updatePermissionDto.getModule());
    }

    /**
     * Removes a permission from all associated roles.
     *
     * @param permission The Permission entity to remove from roles.
     */
    private void removePermissionFromRoles(Permission permission) {
        permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
    }
}
