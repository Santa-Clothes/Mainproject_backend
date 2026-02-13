package com.kdt03.fashion_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.Product512DTO;
import com.kdt03.fashion_api.service.Product512Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "상품 512 벡터 (Product512)", description = "512차원 벡터 임베딩 기반 상품 추천 API")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products-512")
public class Product512Controller {

    private final Product512Service product512Service;

    @Operation(summary = "전체 상품 리스트 조회 (512)", description = "internal_products_512 테이블의 모든 상품 리스트를 반환합니다.")
    @GetMapping("/list")
    public List<Product512DTO> getAllProducts512() {
        return product512Service.getAllProducts512();
    }

    @Operation(summary = "유사 상품 Top 20 조회", description = "벡터 유사도 계산을 통해 입력된 상품과 유사한 상품 20개를 반환합니다.")
    @GetMapping("/similar/{productId}")
    public List<Product512DTO> getSimilarProducts(
            @Parameter(description = "기준 상품 ID", required = true) @PathVariable(value = "productId") String productId) {
        return product512Service.getSimilarProducts(productId);
    }
}
