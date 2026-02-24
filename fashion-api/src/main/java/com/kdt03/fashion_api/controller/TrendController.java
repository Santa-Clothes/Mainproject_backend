package com.kdt03.fashion_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.service.TrendService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "트렌드 분석 (Trends)", description = "쇼핑 인사이트 및 스타일 트렌드 분석 관련 API")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/trends")
@RequiredArgsConstructor // 서비스 주입을 위한 생성자 자동 생성
public class TrendController {
    private final TrendService trendService;

    @Operation(summary = "스타일 트렌드 순위 조회", description = "네이버 쇼핑 인사이트 데이터를 기반으로 분석된 10개 스타일의 통합 트렌드 점수와 순위를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "[{\"style\": \"casual\", \"score\": 85, \"rank\": 1}]")))
    @GetMapping("/shopping-insight")
    public List<Map<String, Object>> getStylesTrend() {
        return trendService.getIntegratedTrend();
    }

    @Operation(summary = "연도별 매출 트렌드 조회", description = "연도별 매출 데이터를 조회합니다. year 파라미터가 없으면 전체 데이터를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "[{\"year\": 2023, \"totalSales\": 1500000}]")))
    @GetMapping("/by-year")
    public ResponseEntity<?> getSalesTrends(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            return ResponseEntity.ok(trendService.getAllTrend());
        }
        return ResponseEntity.ok(trendService.getTrendByYear(year));
    }
}