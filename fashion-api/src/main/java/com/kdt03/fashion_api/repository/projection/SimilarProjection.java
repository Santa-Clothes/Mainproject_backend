package com.kdt03.fashion_api.repository.projection;

public interface SimilarProjection {
    String getProductId();
    String getTitle();
    Integer getPrice();
    String getImageUrl();
    String getProductLink();
    Double getSimilarity();
} 
