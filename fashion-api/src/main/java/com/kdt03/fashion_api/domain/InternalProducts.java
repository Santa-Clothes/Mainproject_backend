package com.kdt03.fashion_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "internal_products")
public class InternalProducts {
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Categories category;

    @Column(columnDefinition = "vector(2048)")
    private float[] embedding;

    @Column(columnDefinition = "TEXT")
    private String style;

    @Column(name = "x_coord")
    private Float xCoord;

    @Column(name = "y_coord")
    private Float yCoord;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
}