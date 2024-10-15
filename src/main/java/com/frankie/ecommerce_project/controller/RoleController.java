package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
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
    public ResponseEntity<ApiResponse<CreateRoleResponse>> createRole(
            @RequestBody CreateRoleResponse createRoleResponse) {
        ApiResponse<CreateRoleResponse> createRole = roleService.createNewRole(createRoleResponse);
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
    public ResponseEntity<ApiResponse<RoleInfo>> getRoleById(@PathVariable String id) {
        ApiResponse<RoleInfo> getRoleById = roleService.getRoleById(id);
        return ResponseEntity.status(HttpStatus.OK).body(getRoleById);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateRoleResponse>> updateRoleById(
            @PathVariable String id, @RequestBody UpdateRoleDto updateRoleDto) {
        ApiResponse<UpdateRoleResponse> updateRole = roleService.updateRoleById(id, updateRoleDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteRoleResponse>> softDeleteRoleById(@PathVariable String id) {
        ApiResponse<DeleteRoleResponse> deleteRole = roleService.softDeleteRoleById(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleteRole);
    }

    @PatchMapping("/reactivate/{id}")
    public ResponseEntity<ApiResponse<ReactivateRoleResponse>> reactivateRoleById(@PathVariable String id) {
        ApiResponse<ReactivateRoleResponse> reactivateRole = roleService.reactivateRoleById(id);
        return ResponseEntity.status(HttpStatus.OK).body(reactivateRole);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<RoleListResponse>> searchRoleByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                    required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                    required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY,
                    required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION
                    , required = false) String sortDir) {
        ApiResponse<RoleListResponse> searchRoleList = roleService.searchRoleByName(name, pageNo,
                pageSize, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(searchRoleList);
    }
}
