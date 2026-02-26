package com.kdt03.fashion_api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "naver_product_vectors_768")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NaverProductVectors768 {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(columnDefinition = "vector")
    private float[] embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_top1")
    private Styles styleTop1;

    @Column(name = "style_score1")
    private Double styleScore1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_top2")
    private Styles styleTop2;

    @Column(name = "style_score2")
    private Double styleScore2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_top3")
    private Styles styleTop3;

    @Column(name = "style_score3")
    private Double styleScore3;
}
