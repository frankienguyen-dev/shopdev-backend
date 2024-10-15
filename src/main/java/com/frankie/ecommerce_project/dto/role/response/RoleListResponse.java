package com.frankie.ecommerce_project.dto.role.response;

import com.frankie.ecommerce_project.dto.role.common.RoleInfo;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleListResponse {
    private MetaData meta;
    private List<RoleInfo> data;
}
