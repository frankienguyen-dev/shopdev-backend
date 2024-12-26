package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.dto.role.request.CreateRoleDto;
import com.frankie.ecommerce_project.dto.role.request.UpdateRoleDto;
import com.frankie.ecommerce_project.dto.role.response.*;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface RoleService {

    ApiResponse<CreateRoleResponse> createNewRole(CreateRoleDto role);

    ApiResponse<RoleListResponse> getAllRoles(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<RoleInfo> getRoleById(String roleId);

    ApiResponse<UpdateRoleResponse> updateRoleById(String roleId, UpdateRoleDto role);

    ApiResponse<DeleteRoleResponse> deleteRoleById(String roleId);
}
