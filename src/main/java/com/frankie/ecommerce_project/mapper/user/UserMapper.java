package com.frankie.ecommerce_project.mapper.user;


import com.frankie.ecommerce_project.dto.user.request.CreateUserDto;
import com.frankie.ecommerce_project.dto.user.response.CreateUserResponse;
import com.frankie.ecommerce_project.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // CreateUserDto to User
    CreateUserDto toCreateUserDto(User user);
    // User to CreateUserDto
    User toUser(CreateUserDto createUserDto);
    // User to CreateUserResponse
    CreateUserResponse toCreateUserResponse(User user);
}
