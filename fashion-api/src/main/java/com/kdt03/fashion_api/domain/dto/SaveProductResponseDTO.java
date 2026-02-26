package com.kdt03.fashion_api.domain.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@lombok.Builder
public class SaveProductResponseDTO {
    private Long saveId;
    private String naverProductId;
    private String title;
    private Integer price;
    private String imageUrl;
    private String productLink;
    private OffsetDateTime createdAt;

    // 512차원 스타일 정보
    private String styleTop1_512;
    private Double styleScore1_512;

    // 768차원 스타일 정보
    private String styleTop1_768;
    private Double styleScore1_768;
}
