package com.frankie.ecommerce_project.service.implement;

import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.response.CreateUserResponse;
import com.frankie.ecommerce_project.mapper.user.UserMapper;
import com.frankie.ecommerce_project.model.User;
import com.frankie.ecommerce_project.repository.UserRepository;
import com.frankie.ecommerce_project.service.UserService;
import com.frankie.ecommerce_project.utils.apiResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .build();
        userRepository.save(newUser);
        CreateUserResponse userDtoMapping = UserMapper.INSTANCE.toCreateUserResponse(newUser);
        return ApiResponse.success("User created successfully", HttpStatus.CREATED, userDtoMapping);
    }
}
