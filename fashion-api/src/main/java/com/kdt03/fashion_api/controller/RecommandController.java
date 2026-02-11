package com.kdt03.fashion_api.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.SimilarProductDTO;
import com.kdt03.fashion_api.service.RecommandService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "상품 추천 (Recommendation)", description = "AI 기반 유사 상품 및 맞춤 추천 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommand")
public class RecommandController {

    private final RecommandService recommandService;

    @Operation(summary = "데모 유사 상품 조회 (ID)", description = "상품 ID를 기반으로 AI가 분석한 유사 상품 리스트를 반환하는 데모 API입니다.")
    @GetMapping("/demo/{productId}")
    public List<SimilarProductDTO> getDemoRecommendations(
            @Parameter(description = "기준이 될 상품 ID", required = true) @PathVariable("productId") String productId) {
        return recommandService.getDemoRecommendations(productId);
    }

    @Operation(summary = "데모 유사 상품 조회 (이미지)", description = "이미지 파일을 업로드하면 AI가 분석하여 유사한 상품 리스트를 반환하는 데모 API입니다.")
    @PostMapping(value = "/demo/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<SimilarProductDTO> uploadDemoRecommendations(
            @Parameter(description = "분석할 이미지 파일", required = true) @RequestParam("file") MultipartFile file) {
        return recommandService.getUploadDemoRecommendations(file);
    }
}
