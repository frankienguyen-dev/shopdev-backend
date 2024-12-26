package com.frankie.ecommerce_project.dto.permission.response;

import com.frankie.ecommerce_project.dto.permission.common.PermissionInfo;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionListResponse implements Serializable {

    private MetaData meta;

    private List<PermissionInfo> data;
}
