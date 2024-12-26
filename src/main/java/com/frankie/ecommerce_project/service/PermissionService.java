package com.frankie.ecommerce_project.service;

import com.frankie.ecommerce_project.dto.permission.common.PermissionInfo;
import com.frankie.ecommerce_project.dto.permission.request.CreatePermissionDto;
import com.frankie.ecommerce_project.dto.permission.request.UpdatePermissionDto;
import com.frankie.ecommerce_project.dto.permission.response.CreatePermissionResponse;
import com.frankie.ecommerce_project.dto.permission.response.DeletePermissionResponse;
import com.frankie.ecommerce_project.dto.permission.response.PermissionListResponse;
import com.frankie.ecommerce_project.dto.permission.response.UpdatePermissionResponse;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;

public interface PermissionService {

    ApiResponse<CreatePermissionResponse> createPermission(CreatePermissionDto permission);

    ApiResponse<PermissionListResponse> getAllPermissions(int pageNo, int pageSize, String sortBy, String sortDir);

    ApiResponse<PermissionInfo> getPermissionById(String permissionId);

    ApiResponse<UpdatePermissionResponse> updatePermissionById(String permissionId, UpdatePermissionDto permission);

    ApiResponse<DeletePermissionResponse> deletePermissionById(String permissionId);

}
