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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling role management requests, including creating, updating, and retrieving role information.
 */
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    /**
     * Constructs RoleController with the role service.
     *
     * @param roleService Service for handling role-related logic
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Creates a new role with the provided details.
     * Restricted to 'ADMIN' role for security.
     *
     * @param createRoleDto Role creation details
     * @return ResponseEntity with ApiResponse containing CreateRoleResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateRoleResponse>> createRole(@RequestBody CreateRoleDto createRoleDto) {
        return buildResponse(roleService.createNewRole(createRoleDto));
    }

    /**
     * Retrieves a paginated list of all roles with sorting options.
     * Restricted to 'ADMIN' role for security.
     *
     * @param pageNo   Page number (default: 0)
     * @param pageSize Number of roles per page (default: 10)
     * @param sortBy   Field to sort by (default: id)
     * @param sortDir  Sort direction (default: asc)
     * @return ResponseEntity with ApiResponse containing RoleListResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<RoleListResponse>> getAllRoles(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        return buildResponse(roleService.getAllRoles(pageNo, pageSize, sortBy, sortDir));
    }

    /**
     * Retrieves role information by its ID.
     * Restricted to 'ADMIN' role for security.
     *
     * @param roleId ID of the role to retrieve
     * @return ResponseEntity with ApiResponse containing RoleInfo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleInfo>> getRoleById(@PathVariable("id") String roleId) {
        return buildResponse(roleService.getRoleById(roleId));
    }

    /**
     * Updates role information for the specified ID.
     * Restricted to 'ADMIN' role for security.
     *
     * @param roleId        ID of the role to update
     * @param updateRoleDto Updated role details
     * @return ResponseEntity with ApiResponse containing UpdateRoleResponse
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateRoleResponse>> updateRole(@PathVariable("id") String roleId, @RequestBody UpdateRoleDto updateRoleDto) {
        return buildResponse(roleService.updateRoleById(roleId, updateRoleDto));
    }

    /**
     * Deletes a role by its ID.
     * Restricted to 'ADMIN' role for security.
     *
     * @param roleId ID of the role to delete
     * @return ResponseEntity with ApiResponse containing DeleteRoleResponse
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteRoleResponse>> deleteRole(@PathVariable("id") String roleId) {
        return buildResponse(roleService.deleteRoleById(roleId));
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
