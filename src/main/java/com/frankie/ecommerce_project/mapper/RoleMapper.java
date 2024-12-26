package com.frankie.ecommerce_project.mapper;

import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.dto.role.response.CreateRoleResponse;
import com.frankie.ecommerce_project.dto.role.response.UpdateRoleResponse;
import com.frankie.ecommerce_project.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    CreateRoleResponse toCreateRoleResponse(Role role);


    RoleInfo toRoleInfo(Role role);

    UpdateRoleResponse toUpdateRoleResponse(Role role);
}
