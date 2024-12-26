package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.dto.role.request.CreateRoleDto;
import com.frankie.ecommerce_project.dto.role.request.UpdateRoleDto;
import com.frankie.ecommerce_project.dto.role.response.*;
import com.frankie.ecommerce_project.service.RoleService;
import com.frankie.ecommerce_project.utils.AppConstants;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateRoleResponse>> createRole(@RequestBody CreateRoleDto role) {
        ApiResponse<CreateRoleResponse> createRole = roleService.createNewRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(createRole);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<RoleListResponse>> getAllRoles(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION
                    , required = false) String sortDir) {
        ApiResponse<RoleListResponse> allRoles = roleService.getAllRoles(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(allRoles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleInfo>> getRoleById(@PathVariable("id") String roleId) {
        ApiResponse<RoleInfo> getRoleById = roleService.getRoleById(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(getRoleById);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateRoleResponse>> updateRoleById(@PathVariable("id") String roleId,
                                                                          @RequestBody UpdateRoleDto role) {
        ApiResponse<UpdateRoleResponse> updateRole = roleService.updateRoleById(roleId, role);
        return ResponseEntity.status(HttpStatus.OK).body(updateRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteRoleResponse>> deleteRoleById(@PathVariable("id") String roleId) {
        ApiResponse<DeleteRoleResponse> deleteRole = roleService.deleteRoleById(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(deleteRole);
    }
}
