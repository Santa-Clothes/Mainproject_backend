package com.kdt03.fashion_api.domain.dto;

import com.kdt03.fashion_api.domain.NaverProducts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverProductDTO {
    private String productId;
    private String title;
    private Integer price;
    private String imageUrl;
    private String productLink;
    private String categoryName;
    private String style;

    public static NaverProductDTO fromEntity(NaverProducts entity) {
        return NaverProductDTO.builder()
                .productId(entity.getProductId())
                .title(entity.getTitle())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .productLink(entity.getProductLink())
                .categoryName(entity.getCategory().getCategoryName())
                .style(entity.getStyle())
                .build();
    }
}
