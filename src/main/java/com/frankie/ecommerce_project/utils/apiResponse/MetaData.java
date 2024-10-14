package com.frankie.ecommerce_project.utils.apiResponse;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaData {
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private Boolean lastPage;
}
