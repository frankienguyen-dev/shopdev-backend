package com.frankie.ecommerce_project.utils.apiResponse;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetails {
    private Instant timestamp;
    private String message;
    private String details;
}
