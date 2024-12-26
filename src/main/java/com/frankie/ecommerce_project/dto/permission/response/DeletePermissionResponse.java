package com.frankie.ecommerce_project.dto.permission.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeletePermissionResponse implements Serializable {

    private String id;

}
