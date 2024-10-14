package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.request.UpdateUserDto;
import com.frankie.ecommerce_project.dto.user.response.*;
import com.frankie.ecommerce_project.exception.ResourceNotFoundException;
import com.frankie.ecommerce_project.mapper.UserMapper;
import com.frankie.ecommerce_project.model.User;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<CreateUserResponse> createNewUser(CreateUserDto createUserDto) {
        try {
            Optional<User> findUserByEmail = userRepository.findByEmail(createUserDto.getEmail());
            if (findUserByEmail.isPresent()) {
                return ApiResponse.error("Email already exists", HttpStatus.BAD_REQUEST, null);
            }
            User newUser = User.builder()
                    .id(createUserDto.getId())
                    .fullName(createUserDto.getFullName())
                    .email(createUserDto.getEmail())
                    .password(passwordEncoder.encode(createUserDto.getPassword()))
                    .phoneNumber(createUserDto.getPhoneNumber())
                    .address(createUserDto.getAddress())
                    .avatar(createUserDto.getAvatar() != null && !createUserDto.getAvatar().isEmpty() ? createUserDto.getAvatar() : null)
                    .dateOfBirth(createUserDto.getDateOfBirth())
                    .isActive(true)
                    .isDeleted(false)
                    .build();
            userRepository.save(newUser);
            CreateUserResponse userDtoMapping = UserMapper.INSTANCE.toCreateUserResponse(newUser);
            return ApiResponse.success("User created successfully", HttpStatus.CREATED, userDtoMapping);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public ApiResponse<UserListResponse> getAllUsers(int pageNo, int pageSize,
                                                     String sortBy, String sortDir) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<User> users = userRepository.findAll(pageable);
        List<UserInfo> userListResponses = getUserInfoList(users);
        UserListResponse response = buildUserListResponse(users, userListResponses);
        return ApiResponse.success(
                "Get all users successfully",
                HttpStatus.OK,
                response
        );
    }

    @Override
    public ApiResponse<UserInfo> getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        UserInfo userInformation = UserMapper.INSTANCE.toUserInfo(user);
        return ApiResponse.success(
                "Get user by id successfully",
                HttpStatus.OK,
                userInformation
        );
    }

    @Override
    public ApiResponse<UpdateUserResponse> updateUserById(String id, UpdateUserDto updateUserDto) {
        User findUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id));
        String newEmail = updateUserDto.getEmail();
        if (newEmail != null && !newEmail.equals(findUser.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(newEmail);
            if (existingUser.isPresent()) {
                return ApiResponse.error("Email already exists", HttpStatus.BAD_REQUEST, null);
            }
        }
        findUser.setFullName(updateUserDto.getFullName());
        findUser.setEmail(updateUserDto.getEmail());
        findUser.setPhoneNumber(updateUserDto.getPhoneNumber());
        findUser.setAddress(updateUserDto.getAddress());
        findUser.setAvatar(updateUserDto.getAvatar());
        findUser.setDateOfBirth(updateUserDto.getDateOfBirth());
        userRepository.save(findUser);
        UpdateUserResponse userDtoMapping = UserMapper.INSTANCE.toUpdateUserResponse(findUser);
        return ApiResponse.success(
                "Update user by id successfully",
                HttpStatus.OK,
                userDtoMapping
        );
    }

    @Override
    public ApiResponse<DeleteUserResponse> softDeleteUserById(String id) {
        User findUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id));
        findUser.setIsDeleted(true);
        findUser.setIsActive(false);
        userRepository.save(findUser);
        DeleteUserResponse deleteUserResponse = DeleteUserResponse.builder()
                .id(findUser.getId())
                .isDeleted(findUser.getIsDeleted())
                .isActive(findUser.getIsActive())
                .updatedBy(findUser.getUpdatedBy())
                .updatedAt(findUser.getUpdatedAt())
                .build();
        return ApiResponse.success(
                "Soft delete user by id successfully",
                HttpStatus.OK,
                deleteUserResponse
        );
    }

    @Override
    public ApiResponse<UserListResponse> searchUserByEmail(int pageNo, int pageSize,
                                                           String sortBy, String sortDir, String email) {
        Pageable pageable = BuildPageable.buildPageable(pageNo, pageSize, sortBy, sortDir);
        Page<User> users = userRepository.findUserByEmail(pageable, email);
        List<UserInfo> userInfo = getUserInfoList(users);
        UserListResponse response = buildUserListResponse(users, userInfo);
        return ApiResponse.success(
                "Search user by email successfully",
                HttpStatus.OK,
                response
        );
    }

    @Override
    public ApiResponse<ReactivateUserAccount> reactivateUserAccount(String id) {
        User findUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id));
        if (findUser.getIsActive() && !findUser.getIsDeleted()) {
            return ApiResponse.error("User is already active", HttpStatus.BAD_REQUEST, null);
        }
        findUser.setIsActive(true);
        findUser.setIsDeleted(false);
        userRepository.save(findUser);
        ReactivateUserAccount reactivateUserAccount = ReactivateUserAccount.builder()
                .id(findUser.getId())
                .isDeleted(findUser.getIsDeleted())
                .isActive(findUser.getIsActive())
                .updatedAt(findUser.getUpdatedAt())
                .updatedBy(findUser.getUpdatedBy())
                .build();
        return ApiResponse.success(
                "Reactivate user account successfully",
                HttpStatus.OK,
                reactivateUserAccount
        );
    }

    private List<UserInfo> getUserInfoList(Page<User> users) {
        return users.getContent().
                stream()
                .map(UserMapper.INSTANCE::toUserInfo)
                .collect(Collectors.toList());
    }

    private UserListResponse buildUserListResponse(Page<User> users, List<UserInfo> userListResponses) {
        MetaData metaData = MetaData.builder()
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .lastPage(users.isLast())
                .build();
        return UserListResponse.builder()
                .meta(metaData)
                .data(userListResponses)
                .build();

    }
}
