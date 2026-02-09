package com.kdt03.fashion_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimilarProductDTO {
    private String productId;
    private String title;
    private String price;
    private String imageUrl;
    private String productLink;
    private Double similarityScore;
}
