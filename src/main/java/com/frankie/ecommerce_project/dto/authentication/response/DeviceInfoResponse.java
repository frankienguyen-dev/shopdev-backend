package com.frankie.ecommerce_project.dto.authentication.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceInfoResponse {
    private String userAgent;
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant lastActive;
    private boolean isActive;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant tokenExpiry;
}
