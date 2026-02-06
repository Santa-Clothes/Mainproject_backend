package com.kdt03.fashion_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class Products {
    @Id
    @Column(name = "product_id")
    private String productId;
    @Column(name = "product_name")
    private String productName;
    private Integer price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;
    @Column(columnDefinition = "TEXT")
    private String style;
}