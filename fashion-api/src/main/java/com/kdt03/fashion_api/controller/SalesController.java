package com.kdt03.fashion_api.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.SalesRankRespDTO;
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

    @Operation(summary = "매장별 기간별 판매 순위 조회", description = "매장별 기간별 판매 순위 Top 5을 조회합니다. storeId 없으면 전체 매장, storeId=online이면 온라인 통합, 매장 ID 입력시 해당 매장 조회")
    @GetMapping("/rank")
    public ResponseEntity<SalesRankRespDTO> getSalesByStore(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "storeId", required = false) String storeId) {
        return ResponseEntity.ok(salesService.getSalesByStore(startDate, endDate, storeId));
    }


}
