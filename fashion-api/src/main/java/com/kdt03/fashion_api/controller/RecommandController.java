package com.kdt03.fashion_api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kdt03.fashion_api.domain.dto.AnalysisResponseDTO;
import com.kdt03.fashion_api.domain.dto.RecommendationResponseDTO;
import com.kdt03.fashion_api.service.ImageUploadService;
import com.kdt03.fashion_api.service.RecommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "상품 추천 (Recommendation)", description = "AI 기반 유사 상품 및 맞춤 추천 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommand")
public class RecommandController {

    private final RecommandService recommandService;
    private final ImageUploadService imageUploadService;

    @Operation(summary = "상품 추천 (기본)", description = "상품 ID를 기반으로 네이버 및 내부 유사 상품 리스트를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추천 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"naverProducts\": [{\"productId\": \"P005\", \"title\": \"네이버 상품\", \"price\": 10000, \"imageUrl\": \"http://...\", \"similarityScore\": 0.92}],\n  \"internalProducts\": [{\"productId\": \"I001\", \"title\": \"내부 상품\", \"price\": 12000, \"imageUrl\": \"http://...\", \"similarityScore\": 0.95}]\n}")))
    @GetMapping("/{productId}")
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> recommand(
            @Parameter(description = "기준이 될 상품 ID", required = true) @PathVariable("productId") String productId) {
        RecommendationResponseDTO result = recommandService.recommand(productId);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("results", new java.util.ArrayList<>());

        response.put("naverProducts", result.getNaverProducts());
        response.put("internalProducts", result.getInternalProducts());

        return org.springframework.http.ResponseEntity.ok(response);
    }

    @Operation(summary = "이미지 분석 요청", description = "이미지를 업로드하고 FastAPI 분석 서버에 분석을 요청합니다. 결과로 분석 데이터를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "분석 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AnalysisResponseDTO.class)))
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public org.springframework.http.ResponseEntity<?> analyze(
            @Parameter(description = "분석할 이미지 파일", required = true) @RequestParam("file") MultipartFile file) {
        try {
            AnalysisResponseDTO result = imageUploadService.uploadAndAnalyze(file);

            // 결과를 더 직관적으로 만들기 위해 맵으로 병합하여 반환 (필요시)
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            if (result.getAnalysisResult() != null) {
                response.putAll(result.getAnalysisResult());
            }
            response.put("naverProducts", result.getNaverProducts());
            response.put("internalProducts", result.getInternalProducts());

            // 응답에서 임베딩값 제외
            response.remove("embedding");

            if (response.containsKey("results") && response.get("results") instanceof java.util.List) {
                java.util.List<?> resultsList = (java.util.List<?>) response.get("results");
                for (Object resObj : resultsList) {
                    if (resObj instanceof java.util.Map) {
                        ((java.util.Map<?, ?>) resObj).remove("latent_vector");
                    }
                }
            }

            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

}