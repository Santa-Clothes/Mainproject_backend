package com.kdt03.fashion_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductXyDTO {
    private String productId;
    private String productName;
    private Double x;
    private Double y;
    private String styleName;
    private String imageUrl;
}
