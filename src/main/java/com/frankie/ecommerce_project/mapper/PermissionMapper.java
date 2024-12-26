package com.frankie.ecommerce_project.mapper;

import com.frankie.ecommerce_project.dto.permission.common.PermissionInfo;
import com.frankie.ecommerce_project.dto.permission.response.CreatePermissionResponse;
import com.frankie.ecommerce_project.dto.permission.response.UpdatePermissionResponse;
import com.frankie.ecommerce_project.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PermissionMapper {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    CreatePermissionResponse toCreatePermissionResponse(Permission permission);

    PermissionInfo toPermissionInfo(Permission permission);

    UpdatePermissionResponse toUpdatePermissionResponse(Permission permission);
}
