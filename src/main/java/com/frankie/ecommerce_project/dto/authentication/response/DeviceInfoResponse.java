package com.frankie.ecommerce_project.dto.authentication.response;

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
    private Instant lastActive;
    private boolean isActive;
    private Instant createdAt;
    private Instant tokenExpiry;
}
