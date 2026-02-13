package com.kdt03.fashion_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product512DTO {
    private String productId;
    private String imageUrl;
    private Double similarity; // 유사도 점수 (optional, 유사도 계산 시에만 사용)
}
