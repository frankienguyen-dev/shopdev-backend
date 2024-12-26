package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.permission.common.PermissionInfo;
import com.frankie.ecommerce_project.dto.permission.request.CreatePermissionDto;
import com.frankie.ecommerce_project.dto.permission.request.UpdatePermissionDto;
import com.frankie.ecommerce_project.dto.permission.response.CreatePermissionResponse;
import com.frankie.ecommerce_project.dto.permission.response.DeletePermissionResponse;
import com.frankie.ecommerce_project.dto.permission.response.PermissionListResponse;
import com.frankie.ecommerce_project.dto.permission.response.UpdatePermissionResponse;
import com.frankie.ecommerce_project.service.PermissionService;
import com.frankie.ecommerce_project.utils.AppConstants;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse<CreatePermissionResponse>> createPermission(@RequestBody CreatePermissionDto permission) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(permission));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<PermissionListResponse>> getAllPermissions(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.getAllPermissions(pageNo, pageSize, sortBy, sortDir));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionInfo>> getPermissionById(@PathVariable("id") String permissionId) {
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.getPermissionById(permissionId));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdatePermissionResponse>> updatePermissionById(@PathVariable("id") String permissionId,
                                                                                      @RequestBody UpdatePermissionDto permission) {
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.updatePermissionById(permissionId, permission));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeletePermissionResponse>> deletePermissionById(@PathVariable("id") String permissionId) {
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.deletePermissionById(permissionId));
    }
}
