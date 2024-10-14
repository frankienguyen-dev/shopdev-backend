package com.frankie.ecommerce_project.mapper;

import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.dto.user.response.CreateUserResponse;
import com.frankie.ecommerce_project.dto.user.response.UpdateUserResponse;
import com.frankie.ecommerce_project.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    CreateUserResponse toCreateUserResponse(User user);

    UserInfo toUserInfo(User user);

    UpdateUserResponse toUpdateUserResponse(User user);
}
