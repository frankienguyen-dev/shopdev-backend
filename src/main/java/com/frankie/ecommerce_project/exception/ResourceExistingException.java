package com.frankie.ecommerce_project.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceExistingException extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private Long fieldValue;
    private String value;

    public ResourceExistingException(String resourceName, String fieldName, Long fieldValue) {
        super(String.format("%s already exists with %s : %s",
                resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceExistingException(String resourceName, String fieldName, String value) {
        super(String.format("%s already exists with %s : %s",
                resourceName, fieldName, value));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.value = value;
    }
}
