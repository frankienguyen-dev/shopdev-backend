package com.frankie.ecommerce_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

/**
 * Controller for handling permission management requests, including creating, updating, and retrieving permission information.
 */
@RestController
@RequestMapping("api/v1/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * Constructs a new PermissionController with the specified service.
     *
     * @param permissionService Service for handling permission-related logic
     */
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    /**
     * Creates a new permission with the provided details.
     * Restricted to 'ADMIN' role for security.
     *
     * @param createPermissionDto Permission creation details
     * @return ResponseEntity with ApiResponse containing CreatePermissionResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreatePermissionResponse>> createPermission(@RequestBody CreatePermissionDto createPermissionDto) {
        return buildResponse(permissionService.createPermission(createPermissionDto));
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

    /**
     * Builds a ResponseEntity from an ApiResponse.
     *
     * @param <T>         Type of the response data
     * @param apiResponse ApiResponse containing data and status
     * @return ResponseEntity with status and body
     */
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(ApiResponse<T> apiResponse) {
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }
}
