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
import com.kdt03.fashion_api.domain.dto.Internal768RecommendationResponseDTO;
import com.kdt03.fashion_api.domain.dto.RecommendationResponseDTO;
import com.kdt03.fashion_api.service.ImageUploadService;
import com.kdt03.fashion_api.service.RecommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "상품 추천 (Recommendation)", description = "AI 기반 유사 상품 및 맞춤 추천 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/recommand")
public class RecommandController {

    private final RecommandService recommandService;
    private final ImageUploadService imageUploadService;

    @Operation(summary = "상품 추천 (기본)", description = "상품 ID를 기반으로 네이버 및 내부 유사 상품 리스트를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "추천 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"naverProducts\": [{\"productId\": \"P005\", \"title\": \"네이버 상품\", \"price\": 10000, \"imageUrl\": \"http://...\", \"similarityScore\": 0.92}],\n  \"internalProducts\": [{\"productId\": \"I001\", \"title\": \"내부 상품\", \"price\": 12000, \"imageUrl\": \"http://...\", \"similarityScore\": 0.95}],\n  \"targetTop1Style\": \"캐주얼\",\n  \"targetTop1Score\": 0.85\n}")))
    @GetMapping("/{productId}")
    public ResponseEntity<RecommendationResponseDTO> recommand(
            @Parameter(description = "기준이 될 상품 ID", required = true) @PathVariable("productId") String productId) {
        RecommendationResponseDTO result = recommandService.recommand(productId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "상품 추천 (768차원)", description = "상품 ID를 기반으로 768차원 벡터 규격의 네이버 및 내부 유사 상품 리스트를 반환합니다.")
    @GetMapping("/768/{productId}")
    public ResponseEntity<RecommendationResponseDTO> recommand768(
            @Parameter(description = "기준이 될 상품 ID", required = true) @PathVariable("productId") String productId) {
        RecommendationResponseDTO result = recommandService.recommand768(productId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "이미지 분석 요청", description = "이미지를 업로드하고 FastAPI 분석 서버에 분석을 요청합니다. 결과로 분석 데이터를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "분석 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"results\": [\n    {\n      \"label\": \"hoodie\",\n      \"box_2d\": [100, 200, 300, 400],\n      \"score\": 0.98\n    }\n  ],\n  \"naverProducts\": [\n    {\n      \"productId\": \"50810757913\",\n      \"title\": \"오버핏 후드티\",\n      \"price\": 29800,\n      \"imageUrl\": \"http://...\",\n      \"similarityScore\": 0.95\n    }\n  ],\n  \"internalProducts\": [\n    {\n      \"productId\": \"I001\",\n      \"title\": \"자사 무지 후드\",\n      \"price\": 25000,\n      \"imageUrl\": \"http://...\",\n      \"similarityScore\": 0.92\n    }\n  ]\n}")))
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

            if (response.get("results") instanceof java.util.List<?> resultsList) {
                for (Object resObj : resultsList) {
                    if (resObj instanceof java.util.Map<?, ?> resMap) {
                        resMap.remove("latent_vector");
                    }
                }
            }

            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during image analysis: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

    @Operation(summary = "768차원 이미지 분석 및 내부 추천", description = "이미지를 업로드하고 768차원 분석 서버(8001)를 통해 내부 유사 상품을 추천받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "분석 및 추천 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\n  \"dimension\": 768,\n  \"styles\": [\n    {\n      \"label\": \"t-shirt\",\n      \"score\": 0.97\n    }\n  ],\n  \"internalProducts\": [\n    {\n      \"productId\": \"N001\",\n      \"title\": \"9온스 베이직 티셔츠\",\n      \"price\": 19000,\n      \"imageUrl\": \"http://...\",\n      \"similarity\": 0.89\n    }\n  ],\n  \"naverProducts\": [\n    {\n      \"productId\": \"50810757913\",\n      \"title\": \"네이버 오버핏 티셔츠\",\n      \"price\": 25000,\n      \"imageUrl\": \"http://...\",\n      \"productLink\": \"http://...\",\n      \"similarity\": 0.85\n    }\n  ]\n}")))
    @PostMapping(value = "/768/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyze768(
            @Parameter(description = "분석할 이미지 파일", required = true) @RequestParam("file") MultipartFile file) {
        try {
            Internal768RecommendationResponseDTO result = recommandService.analyzeInternal768(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

}