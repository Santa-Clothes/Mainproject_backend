package com.kdt03.fashion_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.dto.ProductDTO;
import com.kdt03.fashion_api.domain.dto.ProductMapDTO;
import com.kdt03.fashion_api.domain.dto.ProductMapColumnDTO;
import com.kdt03.fashion_api.domain.dto.StyleCountDTO;
import com.kdt03.fashion_api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepo;

    public List<ProductDTO> findAllProducts(String categoryName) {
        return productRepo.findAllProducts(categoryName);
    }

    public ProductDTO getProductById(String productId) {
        return productRepo.findProductById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public ProductMapColumnDTO getProductMapData() {
        List<ProductMapDTO> list = productRepo.findAllProductMaps();

        return ProductMapColumnDTO.builder()
                .productIds(list.stream().map(ProductMapDTO::getProductId).toList())
                .productNames(list.stream().map(ProductMapDTO::getProductName).toList())
                .styles(list.stream().map(ProductMapDTO::getStyle).toList())
                .xCoords(list.stream().map(ProductMapDTO::getXCoord).toList())
                .yCoords(list.stream().map(ProductMapDTO::getYCoord).toList())
                .build();
    }

    public List<StyleCountDTO> countProductsByStyle() {
        return productRepo.countProductsByStyle();
    }
}
