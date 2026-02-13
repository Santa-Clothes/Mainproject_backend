package com.kdt03.fashion_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.SalesDTO;
import com.kdt03.fashion_api.service.SalesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "판매 정보 (Sales)", description = "판매 순위 및 매출 관련 통계 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    @Operation(summary = "판매 인기 순위 Top 10 조회", description = "가장 많이 판매된 상품 상위 10개를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "[{\"productId\": \"P100\", \"productName\": \"여름 티셔츠\", \"salesCount\": 500, \"rank\": 1}]")))
    @GetMapping("/rank")
    public ResponseEntity<List<SalesDTO>> getTop10BestSellingProducts() {
        List<SalesDTO> top10Products = salesService.getTop10BestSellingProducts();
        return ResponseEntity.ok(top10Products);
    }
}
