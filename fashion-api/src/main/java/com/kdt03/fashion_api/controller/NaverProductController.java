package com.kdt03.fashion_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.NaverProductDTO;
import com.kdt03.fashion_api.service.NaverProductService;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "외부 상품 관리 (Naver Products)", description = "네이버 크롤링 데이터 기반 상품 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/naver-products")
public class NaverProductController {

    private final NaverProductService naverProductService;

    @Operation(summary = "네이버 상품 전체 조회", description = "네이버에서 크롤링한 모든 상품 데이터를 리스트 형식으로 반환합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<NaverProductDTO>> getAllNaverProducts() {
        return ResponseEntity.ok(naverProductService.getAllNaverProducts());
    }
}
