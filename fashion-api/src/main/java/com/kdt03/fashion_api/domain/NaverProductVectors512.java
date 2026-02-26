package com.kdt03.fashion_api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "naver_product_vectors_512")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NaverProductVectors512 {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(columnDefinition = "vector")
    private float[] embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top1_style")
    private Styles top1Style;

    @Column(name = "top1_score")
    private Double top1Score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top2_style")
    private Styles top2Style;

    @Column(name = "top2_score")
    private Double top2Score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top3_style")
    private Styles top3Style;

    @Column(name = "top3_score")
    private Double top3Score;
}
