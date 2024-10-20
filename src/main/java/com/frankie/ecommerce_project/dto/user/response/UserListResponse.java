package com.frankie.ecommerce_project.dto.user.response;

import com.frankie.ecommerce_project.dto.user.common.UserInfo;
import com.frankie.ecommerce_project.utils.apiResponse.MetaData;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListResponse {
    private MetaData meta;
    private List<UserInfo> data;
}
