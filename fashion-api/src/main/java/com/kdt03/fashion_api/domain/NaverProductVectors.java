package com.kdt03.fashion_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "naver_product_vectors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NaverProductVectors {
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(columnDefinition = "vector")
    private float[] embedding;

    @Column(name = "x_coord")
    private Double xCoord;

    @Column(name = "y_coord")
    private Double yCoord;
}
