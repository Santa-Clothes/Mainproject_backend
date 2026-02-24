package com.kdt03.fashion_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesLogDTO {
    private String productId;
    private String productName;
    private Integer saleQuantity;
}
