package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.role.common.RoleName;
import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.request.UpdateUserDto;
import com.frankie.ecommerce_project.dto.user.response.*;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.UserMapper;
import com.frankie.ecommerce_project.model.Role;
import com.frankie.ecommerce_project.model.User;
import com.frankie.ecommerce_project.repository.DeviceRepository;
import com.frankie.ecommerce_project.repository.RefreshTokenRepository;
import com.frankie.ecommerce_project.repository.RoleRepository;
import com.frankie.ecommerce_project.repository.UserRepository;
import com.frankie.ecommerce_project.service.UserService;
import com.frankie.ecommerce_project.utils.BuildPageable;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of UserService for managing user-related operations.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final String SUCCESS_MESSAGE_CREATE = "User created successfully";
    private static final String SUCCESS_MESSAGE_GET_ALL = "Get all users successfully";
    private static final String SUCCESS_MESSAGE_GET_BY_ID = "Get user by id successfully";
    private static final String SUCCESS_MESSAGE_UPDATE = "Update user by id successfully";
    private static final String SUCCESS_MESSAGE_DELETE = "Soft delete user by id successfully";
    private static final String SUCCESS_MESSAGE_SEARCH = "Search user by email successfully";
    private static final String SUCCESS_MESSAGE_REACTIVATE = "Reactivate user account successfully";
    private static final String ERROR_EMAIL_EXISTS = "Email already exists";
    private static final String ERROR_USER_ALREADY_ACTIVE = "User is already active";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DeviceRepository deviceRepository;

    /**
     * Constructs UserServiceImpl with required dependencies.
     *
     * @param userRepository         Repository for user data access
     * @param passwordEncoder        Encoder for password hashing
     * @param roleRepository         Repository for role data access
     * @param refreshTokenRepository Repository for refresh token data access
     */
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository,
                           DeviceRepository deviceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.deviceRepository = deviceRepository;
    }

    /**
     * Creates a new user with the provided details.
     *
     * @param createUserDto User creation details
     * @return ApiResponse containing the created user's information
     * @throws ResourceNotFoundException if a role is not found
     */
    @Transactional
    @Override
    public ApiResponse<CreateUserResponse> createNewUser(CreateUserDto createUserDto) {
        validateEmailNotExists(createUserDto.getEmail());
        Set<Role> roles = buildRoleList(createUserDto.getRoles());
        User newUser = buildNewUser(createUserDto, roles);
        userRepository.save(newUser);
        CreateUserResponse response = UserMapper.INSTANCE.toCreateUserResponse(newUser);
        return ApiResponse.success(SUCCESS_MESSAGE_CREATE, HttpStatus.CREATED, response);
    }

    /**
     * Builds a set of roles from role names.
     *
     * @param roleNames Set of role names
     * @return Set of Role entities
     * @throws ResourceNotFoundException if a role is not found
     */
    private Set<Role> buildRoleList(Set<RoleName> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) return new HashSet<>();
        return roleNames.stream()
                .map(roleName -> roleRepository.findByNameWithPermissions(roleName.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName.getName())))
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageNo   Page number (zero-based)
     * @param pageSize Number of items per page
     * @param sortBy   Field to sort by
     * @param sortDir  Sort direction (asc/desc)
     * @return ApiResponse containing a list of users and pagination metadata
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<UserListResponse> getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<User> users = userRepository.findAllWithRoles(pageable);
        return buildUserListResponse(users, SUCCESS_MESSAGE_GET_ALL);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId User ID
     * @return ApiResponse containing user information
     * @throws ResourceNotFoundException if a user is not found
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<UserInfo> getUserById(String userId) {
        User user = findUserById(userId);
        UserInfo userInformation = UserMapper.INSTANCE.toUserInfo(user);
        return ApiResponse.success(SUCCESS_MESSAGE_GET_BY_ID, HttpStatus.OK, userInformation);
    }

    /**
     * Updates a user by their ID with the provided details.
     *
     * @param userId        User ID
     * @param updateUserDto Updated user details
     * @return ApiResponse containing the updated user's information
     * @throws ResourceNotFoundException if a user or role is not found
     */
    @Transactional
    @Override
    public ApiResponse<UpdateUserResponse> updateUserById(String userId, UpdateUserDto updateUserDto) {
        User user = findUserById(userId);
        validateEmailUpdate(user, updateUserDto.getEmail());
        Set<Role> roles = buildRoleList(updateUserDto.getRoles());
        updateUser(user, updateUserDto, roles);
        userRepository.save(user);
        UpdateUserResponse response = UserMapper.INSTANCE.toUpdateUserResponse(user);
        return ApiResponse.success(SUCCESS_MESSAGE_UPDATE, HttpStatus.OK, response);
    }

    /**
     * Softly deletes a user by their ID.
     *
     * @param userId User ID
     * @return ApiResponse containing deletion details
     * @throws ResourceNotFoundException if a user is not found
     */
    @Transactional
    @Override
    public ApiResponse<DeleteUserResponse> softDeleteUserById(String userId) {
        User user = findUserById(userId);
        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
        refreshTokenRepository.deleteByUser(user);
        deviceRepository.deactivateDevicesByUser(user);
        DeleteUserResponse response = buildDeleteUserResponse(user);
        return ApiResponse.success(SUCCESS_MESSAGE_DELETE, HttpStatus.OK, response);
    }

    /**
     * Searches users by email with pagination.
     *
     * @param pageNo    Page number (zero-based)
     * @param pageSize  Number of items per page
     * @param sortBy    Field to sort by
     * @param sortDir   Sort direction (asc/desc)
     * @param userEmail Email to search for
     * @return ApiResponse containing a list of matching users and pagination metadata
     */
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<UserListResponse> searchUserByEmail(int pageNo, int pageSize,
                                                           String sortBy, String sortDir, String userEmail) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<User> users = userRepository.findUserByEmailWithRoles(userEmail, pageable);
        return buildUserListResponse(users, SUCCESS_MESSAGE_SEARCH);
    }

    /**
     * Reactivates a user account by their ID.
     *
     * @param userId User ID
     * @return ApiResponse containing reactivation details
     * @throws ResourceNotFoundException if a user is not found
     */
    @Transactional
    @Override
    public ApiResponse<ReactivateUserAccount> reactivateUserAccount(String userId) {
        User user = findUserById(userId);
        if (user.getIsActive() && !user.getIsDeleted()) {
            return ApiResponse.error(ERROR_USER_ALREADY_ACTIVE, HttpStatus.BAD_REQUEST, null);
        }
        user.setIsActive(true);
        user.setIsDeleted(false);
        userRepository.save(user);
        ReactivateUserAccount response = buildReactivateUserResponse(user);
        return ApiResponse.success(SUCCESS_MESSAGE_REACTIVATE, HttpStatus.OK, response);
    }

    /**
     * Maps a page of users to a list of UserInfo.
     *
     * @param users Page of User entities
     * @return List of UserInfo DTOs
     */
    private List<UserInfo> mapUsersToUserInfo(Page<User> users) {
        return users.getContent().
                stream()
                .map(UserMapper.INSTANCE::toUserInfo)
                .collect(Collectors.toList());
    }

    /**
     * Builds a UserListResponse from a page of users.
     *
     * @param users   Page of User entities
     * @param message Success message
     * @return ApiResponse containing UserListResponse
     */
    private ApiResponse<UserListResponse> buildUserListResponse(Page<User> users, String message) {
        List<UserInfo> userInfoList = mapUsersToUserInfo(users);
        MetaData metaData = MetaData.builder()
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .lastPage(users.isLast())
                .build();
        UserListResponse response = UserListResponse.builder()
                .meta(metaData)
                .data(userInfoList)
                .build();
        return ApiResponse.success(message, HttpStatus.OK, response);
    }

    /**
     * Validates that an email is not already in use.
     *
     * @param email Email to check
     * @throws IllegalArgumentException if email exists
     */
    private void validateEmailNotExists(String email) {
        userRepository.findByEmailWithRoles(email).ifPresent(user -> {
            throw new IllegalArgumentException(ERROR_EMAIL_EXISTS);
        });
    }

    /**
     * Builds a new User entity from the provided CreateUserDto and roles.
     *
     * @param createUserDto DTO containing user creation details
     * @param roles         Set of roles to assign to the user
     * @return User entity with initialized fields
     */
    private User buildNewUser(CreateUserDto createUserDto, Set<Role> roles) {
        return User.builder()
                .id(createUserDto.getId())
                .fullName(createUserDto.getFullName())
                .email(createUserDto.getEmail())
                .password(passwordEncoder.encode(createUserDto.getPassword()))
                .phoneNumber(createUserDto.getPhoneNumber())
                .roles(roles)
                .address(createUserDto.getAddress())
                .avatar(createUserDto.getAvatar() != null && !createUserDto.getAvatar().isEmpty() ? createUserDto.getAvatar() : null)
                .dateOfBirth(createUserDto.getDateOfBirth())
                .isActive(true)
                .isDeleted(false)
                .isVerified(true)
                .build();
    }

    /**
     * Finds a user by ID or throws an exception.
     *
     * @param userId User ID
     * @return User entity
     * @throws ResourceNotFoundException if a user is not found
     */
    private User findUserById(String userId) {
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * Validates email updates to ensure uniqueness.
     *
     * @param user     User being updated
     * @param newEmail New email address
     * @throws IllegalArgumentException if a new email is already in use
     */
    private void validateEmailUpdate(User user, String newEmail) {
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            validateEmailNotExists(newEmail);
        }
    }

    /**
     * Updates user fields with new values from DTO.
     *
     * @param user          User to update
     * @param updateUserDto Updated user details
     * @param roles         Set of roles
     */
    private void updateUser(User user, UpdateUserDto updateUserDto, Set<Role> roles) {
        user.setFullName(updateUserDto.getFullName());
        user.setEmail(updateUserDto.getEmail());
        user.setPhoneNumber(updateUserDto.getPhoneNumber());
        user.setAddress(updateUserDto.getAddress());
        user.setAvatar(updateUserDto.getAvatar());
        user.setDateOfBirth(updateUserDto.getDateOfBirth());
        user.setRoles(roles);
    }

    /**
     * Builds a DeleteUserResponse from a user.
     *
     * @param user User entity
     * @return DeleteUserResponse DTO
     */
    private DeleteUserResponse buildDeleteUserResponse(User user) {
        return DeleteUserResponse.builder()
                .id(user.getId())
                .isDeleted(user.getIsDeleted())
                .isActive(user.getIsActive())
                .updatedBy(user.getUpdatedBy())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Builds a ReactivateUserAccount from a user.
     *
     * @param user User entity
     * @return ReactivateUserAccount DTO
     */
    private ReactivateUserAccount buildReactivateUserResponse(User user) {
        return ReactivateUserAccount.builder()
                .id(user.getId())
                .isDeleted(user.getIsDeleted())
                .isActive(user.getIsActive())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
}
