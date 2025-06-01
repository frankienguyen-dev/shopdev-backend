package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.permission.common.PermissionName;
import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.dto.role.request.CreateRoleDto;
import com.frankie.ecommerce_project.dto.role.request.UpdateRoleDto;
import com.frankie.ecommerce_project.dto.role.response.*;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.RoleMapper;
import com.frankie.ecommerce_project.model.Permission;
import com.frankie.ecommerce_project.model.Role;
import com.frankie.ecommerce_project.model.User;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }


    @Override
    public ApiResponse<CreateRoleResponse> createNewRole(CreateRoleDto role) {

        Optional<Role> findRoleByName = roleRepository.findByNameWithPermissions(role.getName());

        if (findRoleByName.isPresent()) {
            return ApiResponse.error("Role already exists", HttpStatus.BAD_REQUEST, null);
        }

        Set<Permission> permissions = buildPermissionList(role.getPermissions());

        Role newRole = Role.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(permissions)
                .isDeleted(false)
                .build();

        roleRepository.save(newRole);

        CreateRoleResponse roleDtoMapping = RoleMapper.INSTANCE.toCreateRoleResponse(newRole);

        return ApiResponse.success("Role created successfully", HttpStatus.CREATED, roleDtoMapping);
    }



    private Set<Permission> buildPermissionList(Set<PermissionName> permissions) {

        Set<Permission> permissionList = new HashSet<>();

        for (PermissionName permission : permissions) {

            Optional<Permission> findPermission = permissionRepository.findByName(permission.getName());

            if (findPermission.isPresent()) permissionList.add(findPermission.get());

            else throw new ResourceNotFoundException("Permission name", "name", permission.getName());
        }

        return permissionList;
    }

    @Override
    public ApiResponse<RoleListResponse> getAllRoles(int pageNo, int pageSize, String sortBy,
                                                     String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Role> roles = roleRepository.findAll(pageable);
        List<RoleInfo> roleInfoList = buildRoleList(roles);
        RoleListResponse roleListResponses = buildRoleListResponse(roles, roleInfoList);
        return ApiResponse.success("Get all roles successfully", HttpStatus.OK, roleListResponses);
    }

    @Override
    public ApiResponse<RoleInfo> getRoleById(String roleId) {
        Role findRole = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role id", "id", roleId));
        RoleInfo roleInfo = RoleMapper.INSTANCE.toRoleInfo(findRole);
        return ApiResponse.success("Get role by id successfully", HttpStatus.OK, roleInfo);
    }

    @Override
    public ApiResponse<UpdateRoleResponse> updateRoleById(String roleId, UpdateRoleDto role) {

        Optional<Role> existingRole = roleRepository.findByNameWithPermissions(role.getName());

        if (existingRole.isPresent() && !existingRole.get().getName().equalsIgnoreCase(role.getName())) {
            return ApiResponse.error("Role already exists", HttpStatus.BAD_REQUEST, null);
        }

        Role findRole = roleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException("Role id", "id", roleId));

        Set<Permission> permissions = buildPermissionList(role.getPermissions());

        findRole.setName(role.getName());
        findRole.setPermissions(permissions);

        roleRepository.save(findRole);

        UpdateRoleResponse roleResponse = RoleMapper.INSTANCE.toUpdateRoleResponse(findRole);

        return ApiResponse.success("Update role successfully", HttpStatus.OK, roleResponse);
    }

    @Override
    public ApiResponse<DeleteRoleResponse> deleteRoleById(String roleId) {

        Role findRole = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role id", "id", roleId));

        Set<User> users = findRole.getUser();

        for (User user : users) {
            user.getRoles().remove(findRole);
        }

        roleRepository.delete(findRole);

        DeleteRoleResponse deleteRoleResponse = DeleteRoleResponse.builder().id(findRole.getId()).build();

        return ApiResponse.success("Role deleted successfully", HttpStatus.OK, deleteRoleResponse);
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
