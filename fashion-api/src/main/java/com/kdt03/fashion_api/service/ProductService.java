package com.kdt03.fashion_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.dto.ProductDTO;
import com.kdt03.fashion_api.domain.dto.ProductMapDTO;
import com.kdt03.fashion_api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepo;

    public List<ProductDTO> findAllProducts() {
        return productRepo.findAllProducts();
    }

    public ProductDTO getProductById(String productId) {
        return productRepo.findProductById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public List<ProductMapDTO> getProductMapData() {
        return productRepo.findAllProductMaps();
    }

    public java.util.Map<String, List<?>> getProductMapDataColumnar() {
        List<ProductMapDTO> data = productRepo.findAllProductMaps();

        java.util.Map<String, List<?>> result = new java.util.HashMap<>();
        result.put("productId", data.stream().map(ProductMapDTO::getProductId).toList());
        result.put("productName", data.stream().map(ProductMapDTO::getProductName).toList());
        result.put("style", data.stream().map(ProductMapDTO::getStyle).toList());
        result.put("xCoord", data.stream().map(ProductMapDTO::getXCoord).toList());
        result.put("yCoord", data.stream().map(ProductMapDTO::getYCoord).toList());

        return result;
    }
}