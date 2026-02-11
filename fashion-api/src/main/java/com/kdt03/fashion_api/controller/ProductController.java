package com.kdt03.fashion_api.controller;

import com.kdt03.fashion_api.domain.dto.ProductDTO;
import com.kdt03.fashion_api.domain.dto.ProductMapColumnDTO;
import com.kdt03.fashion_api.domain.dto.StyleCountDTO;
import com.kdt03.fashion_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "상품 관리 (Product)", description = "상품 목록 조회 및 통계 관련 API")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "카테고리별로 필터링된 상품 목록을 조회합니다. 파라미터가 없으면 전체 목록을 반환합니다.")
    @GetMapping("/list")
    public List<ProductDTO> getProducts(
            @Parameter(description = "필터링할 카테고리 이름 (예: 아우터, 상의)") @RequestParam(value = "categoryName", required = false) String categoryName) {
        return productService.findAllProducts(categoryName);
    }

    @Operation(summary = "상품 상세 정보 조회", description = "상품 ID를 기반으로 특정 상품의 상세 정보를 조회합니다.")
    @GetMapping("/detail")
    public ResponseEntity<ProductDTO> getProduct(
            @Parameter(description = "조회할 상품의 ID", required = true) @RequestParam("productId") String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @Operation(summary = "상품 지도 데이터 조회", description = "상품 지도(Map) 구성을 위한 모든 상품의 위치 좌표 및 스타일 정보를 조회합니다.")
    @GetMapping("/map")
    public ProductMapColumnDTO getProductMap() {
        return productService.getProductMapData();
    }

    @Operation(summary = "스타일별 상품 통계", description = "시스템 내 상품들의 스타일별 분포 개수를 집계하여 개수 내림차순으로 반환합니다.")
    @GetMapping("/style-count")
    public List<StyleCountDTO> getStyleCounts() {
        return productService.countProductsByStyle();
    }
}
