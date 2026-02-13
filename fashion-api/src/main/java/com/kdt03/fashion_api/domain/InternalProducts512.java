package com.kdt03.fashion_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "internal_products_512")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalProducts512 {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "image_url")
    private String imageUrl;

    // pgvector의 vector 타입은 String으로 매핑
    @Column(name = "embedding", columnDefinition = "vector")
    private String embedding;
}
