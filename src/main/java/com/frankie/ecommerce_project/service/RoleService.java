package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.dto.role.request.UpdateRoleDto;
import com.frankie.ecommerce_project.dto.role.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface RoleService {
    ApiResponse<CreateRoleResponse> createNewRole(CreateRoleResponse createRoleResponse);

    ApiResponse<RoleListResponse> getAllRoles(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<RoleInfo> getRoleById(String id);

    ApiResponse<UpdateRoleResponse> updateRoleById(String id, UpdateRoleDto updateRoleDto);

    ApiResponse<DeleteRoleResponse> softDeleteRoleById(String id);

    ApiResponse<ReactivateRoleResponse> reactivateRoleById(String id);

    ApiResponse<RoleListResponse> searchRoleByName(String name, int pageNo, int pageSize, String sortBy,
                                                   String sortDir);
}
