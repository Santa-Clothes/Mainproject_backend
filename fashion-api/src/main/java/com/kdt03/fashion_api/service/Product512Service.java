package com.kdt03.fashion_api.service;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.dto.Product512DTO;
import com.kdt03.fashion_api.repository.InternalProducts512Repository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class Product512Service {

    private final InternalProducts512Repository product512Repo;

    // 전체 리스트 조회
    public List<Product512DTO> getAllProducts512() {
        return product512Repo.findAllProducts512();
    }

    // 유사 상품 Top 20 조회
    public List<Product512DTO> getSimilarProducts(String productId) {
        List<Object[]> results = product512Repo.findTop20SimilarProducts(productId);

        return results.stream()
                .map(row -> Product512DTO.builder()
                        .productId((String) row[0])
                        .imageUrl((String) row[1])
                        .similarity(((Number) row[2]).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }
}
