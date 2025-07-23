package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.permission.common.PermissionName;
import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.dto.role.request.CreateRoleDto;
import com.frankie.ecommerce_project.dto.role.request.UpdateRoleDto;
import com.frankie.ecommerce_project.dto.role.response.*;
import com.frankie.ecommerce_project.exception.ResourceExistingException;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.RoleMapper;
import com.frankie.ecommerce_project.model.Permission;
import com.frankie.ecommerce_project.model.Role;
import com.frankie.ecommerce_project.repository.PermissionRepository;
import com.frankie.ecommerce_project.repository.RoleRepository;
import com.frankie.ecommerce_project.service.RoleService;
import com.frankie.ecommerce_project.utils.BuildPageable;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of RoleService for managing role-related operations.
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    private static final String SUCCESS_MESSAGE_CREATE = "Role created successfully";
    private static final String SUCCESS_MESSAGE_GET_ALL = "Get all roles successfully";
    private static final String SUCCESS_MESSAGE_GET_BY_ID = "Get role by id successfully";
    private static final String SUCCESS_MESSAGE_UPDATE = "Update role successfully";
    private static final String SUCCESS_MESSAGE_DELETE = "Role deleted successfully";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * Constructs RoleServiceImpl with required dependencies.
     *
     * @param roleRepository       Repository for role data access
     * @param permissionRepository Repository for permission data access
     */
    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }


    /**
     * Creates a new role with the provided details.
     *
     * @param createRoleDto Role creation details
     * @return ApiResponse containing the created role's information
     * @throws ResourceNotFoundException if a permission is not found
     */
    @Transactional
    @Override
    public ApiResponse<CreateRoleResponse> createNewRole(CreateRoleDto createRoleDto) {
        checkRoleExists(createRoleDto.getName(), null);
        Set<Permission> permissions = mapToPermissionSet(createRoleDto.getPermissions());
        Role newRole = Role.builder()
                .id(createRoleDto.getId())
                .name(createRoleDto.getName())
                .permissions(permissions)
                .isDeleted(false)
                .build();
        roleRepository.save(newRole);
        CreateRoleResponse roleDtoMapping = RoleMapper.INSTANCE.toCreateRoleResponse(newRole);
        return ApiResponse.success(SUCCESS_MESSAGE_CREATE, HttpStatus.CREATED, roleDtoMapping);
    }

    /**
     * Retrieves a paginated list of all roles with their permissions.
     *
     * @param pageNo   Page number (zero-based)
     * @param pageSize Number of items per page
     * @param sortBy   Field to sort by
     * @param sortDir  Sort direction (asc/desc)
     * @return ApiResponse containing a list of roles and pagination metadata
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<RoleListResponse> getAllRoles(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Role> roles = roleRepository.findAllWithPermissions(pageable);
        return buildRoleListResponse(roles);
    }

    /**
     * Retrieves a role by its ID with its permissions.
     *
     * @param roleId Role ID
     * @return ApiResponse containing role information
     * @throws ResourceNotFoundException if the role is not found
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<RoleInfo> getRoleById(String roleId) {
        Role role = findRoleById(roleId);
        RoleInfo roleInfo = RoleMapper.INSTANCE.toRoleInfo(role);
        return ApiResponse.success(SUCCESS_MESSAGE_GET_BY_ID, HttpStatus.OK, roleInfo);
    }

    /**
     * Updates a role by its ID with the provided details.
     *
     * @param roleId        Role ID
     * @param updateRoleDto Updated role details
     * @return ApiResponse containing the updated role's information
     * @throws ResourceNotFoundException if the role or a permission is not found
     */
    @Transactional
    @Override
    public ApiResponse<UpdateRoleResponse> updateRoleById(String roleId, UpdateRoleDto updateRoleDto) {
        checkRoleExists(updateRoleDto.getName(), roleId);
        Role role = findRoleById(roleId);
        Set<Permission> permissions = mapToPermissionSet(updateRoleDto.getPermissions());
        role.setName(updateRoleDto.getName());
        role.setPermissions(permissions);
        roleRepository.save(role);
        UpdateRoleResponse response = RoleMapper.INSTANCE.toUpdateRoleResponse(role);
        return ApiResponse.success(SUCCESS_MESSAGE_UPDATE, HttpStatus.OK, response);
    }

    /**
     * Deletes a role by its ID and removes it from associated users.
     *
     * @param roleId Role ID
     * @return ApiResponse containing deletion details
     * @throws ResourceNotFoundException if the role is not found
     */
    @Transactional
    @Override
    public ApiResponse<DeleteRoleResponse> deleteRoleById(String roleId) {
        Role role = findRoleById(roleId);
        role.getUser().forEach(user -> user.getRoles().remove(role));
        roleRepository.delete(role);
        DeleteRoleResponse response = DeleteRoleResponse.builder().id(role.getId()).build();
        return ApiResponse.success(SUCCESS_MESSAGE_DELETE, HttpStatus.OK, response);
    }

    /**
     * Maps a page of roles to a list of RoleInfo DTOs.
     *
     * @param roles Page of Role entities
     * @return List of RoleInfo DTOs
     */
    private List<RoleInfo> mapToRoleInfoList(Page<Role> roles) {
        return roles.getContent().stream()
                .map(RoleMapper.INSTANCE::toRoleInfo)
                .collect(Collectors.toList());
    }

    /**
     * Builds an ApiResponse containing a paginated list of roles and associated metadata.
     *
     * @param roles Page of Role entities retrieved from the database
     * @return ApiResponse object containing RoleListResponse data and pagination metadata
     */
    private ApiResponse<RoleListResponse> buildRoleListResponse(Page<Role> roles) {
        List<RoleInfo> roleInfoList = mapToRoleInfoList(roles);
        MetaData metaData = MetaData.builder()
                .pageNo(roles.getNumber())
                .pageSize(roles.getSize())
                .totalPages(roles.getTotalPages())
                .totalElements(roles.getTotalElements())
                .lastPage(roles.isLast())
                .build();
        RoleListResponse response = RoleListResponse.builder()
                .meta(metaData)
                .data(roleInfoList)
                .build();
        return ApiResponse.success(RoleServiceImpl.SUCCESS_MESSAGE_GET_ALL, HttpStatus.OK, response);
    }

    /**
     * Checks if a role name already exists, excluding the role with the given ID (if any).
     *
     * @param roleName Role name to check
     * @param roleId   ID of the role to exclude (null for creation, non-null for update)
     * @throws ResourceNotFoundException if the role name already exists
     */
    private void checkRoleExists(String roleName, String roleId) {
        roleRepository.findByNameWithPermissions(roleName)
                .filter(role -> roleId == null || !Objects.equals(role.getId(), roleId))
                .ifPresent(role -> {
                    throw new ResourceExistingException("Role", "name", roleName);
                });
    }

    /**
     * Maps a set of permission names to a set of Permission entities.
     *
     * @param permissionNames Set of permission names
     * @return Set of Permission entities
     * @throws ResourceNotFoundException if a permission is not found
     */
    private Set<Permission> mapToPermissionSet(Set<PermissionName> permissionNames) {
        if(permissionNames == null || permissionNames.isEmpty()) {
            return new HashSet<>();
        }
        List<String> names = permissionNames.stream()
                .map(PermissionName::getName)
                .collect(Collectors.toList());
        List<Permission> permissions = permissionRepository.findByNameIn(names);
        if (permissions.size() != names.size()) {
            throw new ResourceNotFoundException("Permission", "name", "Some permissions not found");
        }
        return new HashSet<>(permissions);
    }

    /**
     * Finds a role by ID or throws an exception if not found.
     *
     * @param roleId Role ID
     * @return Role entity
     * @throws ResourceNotFoundException if the role is not found
     */
    private Role findRoleById(String roleId) {
        return roleRepository.findByIdWithPermissions(roleId).orElseThrow(() -> new ResourceNotFoundException("Role id", "id", roleId));
    }
}
