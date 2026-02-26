package com.kdt03.fashion_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nineounce_product_xy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NineounceProductXy {

    @Id
    @Column(name = "product_id")
    private String productId;

    @jakarta.persistence.OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private InternalProducts product;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "top1_style")
    private Styles top1Style;

    private Double x;
    private Double y;
}
