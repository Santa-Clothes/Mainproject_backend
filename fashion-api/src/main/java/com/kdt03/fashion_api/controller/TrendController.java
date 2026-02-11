package com.kdt03.fashion_api.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Operation(summary = "스타일 트렌드 순위 조회", description = "네이버 쇼핑 인사이트 데이터를 기반으로 분석된 23개 스타일의 통합 트렌드 점수와 순위를 반환합니다.")
    @GetMapping("/shopping-insight")
    public List<Map<String, Object>> getStylesTrend() {
        // 서비스에서 계산된 23개 스타일 순위 리스트를 반환
        // 스프링이 알아서 JSON 배열 [{ "style": "히피", "score": 100.0 }, ...] 로 변환함
        return trendService.getIntegratedTrend();
    }
}