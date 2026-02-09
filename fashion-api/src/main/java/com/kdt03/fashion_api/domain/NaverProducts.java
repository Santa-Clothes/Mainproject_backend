package com.kdt03.fashion_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "naver_products")
public class NaverProducts {
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(columnDefinition = "TEXT")
    private String title;

    private Integer price;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "product_link", columnDefinition = "TEXT")
    private String productLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Column(name = "category_id")
    private Categories category;

    @Column(columnDefinition = "vector(2048)")
    private float[] embedding;

    @Column(columnDefinition = "TEXT")
    private String style;
}
