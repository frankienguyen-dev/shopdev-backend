package com.frankie.ecommerce_project.controller;

import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.request.UpdateUserDto;
import com.frankie.ecommerce_project.dto.user.response.*;
import com.frankie.ecommerce_project.service.UserService;
import com.frankie.ecommerce_project.utils.AppConstants;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user management requests, including creating, updating, and retrieving user information.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Constructs UserController with the user service.
     *
     * @param userService Service for handling user-related logic
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(@RequestBody CreateUserDto createUserDto) {
        return buildResponse(userService.createNewUser(createUserDto));
    }

    /**
     * Retrieves a paginated list of all users with sorting options.
     * Restricted to 'ADMIN' role for security.
     *
     * @param pageNo   Page number (default: 0)
     * @param pageSize Number of users per page (default: 10)
     * @param sortBy   Field to sort by (default: id)
     * @param sortDir  Sort direction (default: asc)
     * @return ResponseEntity with ApiResponse containing UserListResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        return buildResponse(userService.getAllUsers(pageNo, pageSize, sortBy, sortDir));
    }

    /**
     * Retrieves user information by their ID.
     * Accessible to 'ADMIN' or the user themselves (matching principal id).
     *
     * @param userId ID of the user to retrieve
     * @return ResponseEntity with ApiResponse containing UserInfo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserInfo>> getUserById(@PathVariable("id") String userId) {
        return buildResponse(userService.getUserById(userId));
    }

    /**
     * Updates user information for the specified ID.
     * Accessible to 'ADMIN' or the user themselves (matching principal id).
     *
     * @param userId        ID of the user to update
     * @param updateUserDto Updated user details
     * @return ResponseEntity with ApiResponse containing UpdateUserResponse
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUser(@PathVariable("id") String userId, @RequestBody UpdateUserDto updateUserDto) {
        return buildResponse(userService.updateUserById(userId, updateUserDto));
    }

    /**
     * Softly deletes a user by their ID.
     * Restricted to 'ADMIN' role for security.
     *
     * @param userId ID of the user to softly delete
     * @return ResponseEntity with ApiResponse containing DeleteUserResponse
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> softDeleteUser(@PathVariable("id") String userId) {
        return buildResponse(userService.softDeleteUserById(userId));
    }

    /**
     * Searches for users by email with pagination and sorting options.
     * Restricted to 'ADMIN' role for security.
     *
     * @param pageNo   Page number (default: 0)
     * @param pageSize Number of users per page (default: 10)
     * @param sortBy   Field to sort by (default: id)
     * @param sortDir  Sort direction (default: asc)
     * @param email    Email to search for (optional)
     * @return ResponseEntity with ApiResponse containing UserListResponse
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserListResponse>> searchUsersByEmail(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir,
            @RequestParam(value = "email", required = false) String email) {
        return buildResponse(userService.searchUserByEmail(pageNo, pageSize, sortBy, sortDir, email));
    }

    /**
     * Reactivates a deactivated user account by their ID.
     * Restricted to 'ADMIN' role for security.
     *
     * @param userId ID of the user to reactivate
     * @return ResponseEntity with ApiResponse containing ReactivateUserAccount
     */
    @PatchMapping("/reactivate/{id}")
    public ResponseEntity<ApiResponse<ReactivateUserAccount>> reactivateUser(@PathVariable("id") String userId) {
        return buildResponse(userService.reactivateUserAccount(userId));
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
