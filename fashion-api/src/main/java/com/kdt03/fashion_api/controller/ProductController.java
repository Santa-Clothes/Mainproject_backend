package com.kdt03.fashion_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.ProductDTO;
import com.kdt03.fashion_api.domain.dto.ProductMapColumnDTO;
import com.kdt03.fashion_api.domain.dto.StyleCountDTO;
import com.kdt03.fashion_api.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "상품 관리 (Product)", description = "상품 목록 조회 및 통계 관련 API")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "카테고리별로 필터링된 상품 목록을 조회합니다. 파라미터가 없으면 전체 목록을 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "[{\"id\": \"P123\", \"name\": \"겨울 코트\", \"price\": 150000, \"category\": \"아우터\", \"imageUrl\": \"http://...\"}]")))
    @GetMapping("/list")
    public List<ProductDTO> getProducts(
            @Parameter(description = "필터링할 카테고리 이름 (예: 자켓, 바지, 스커트...)") @RequestParam(value = "categoryName", required = false) String categoryName) {
        return productService.findAllProducts(categoryName);
    }

    @Operation(summary = "상품 상세 정보 조회", description = "상품 ID를 기반으로 특정 상품의 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"id\": \"P123\", \"name\": \"겨울 코트\", \"price\": 150000, \"description\": \"따뜻한 겨울 코트입니다.\", \"imageUrl\": \"http://...\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 없음", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "상품을 찾을 수 없습니다.")))
    })
    @GetMapping("/detail")
    public ResponseEntity<ProductDTO> getProduct(
            @Parameter(description = "조회할 상품의 ID", required = true) @RequestParam("productId") String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @Operation(summary = "상품 지도 데이터 조회 (512차원)", description = "512차원 벡터 기반의 상품 위치 좌표 정보를 조회합니다.")
    @GetMapping("/map/512")
    public ProductMapColumnDTO getProductMap512() {
        return productService.getProductMapData512();
    }

    @Operation(summary = "상품 지도 데이터 조회 (768차원)", description = "768차원 벡터 기반의 상품 위치 좌표 정보를 조회합니다.")
    @GetMapping("/map/768")
    public ProductMapColumnDTO getProductMap768() {
        return productService.getProductMapData768();
    }

    @Operation(summary = "스타일별 상품 통계 (512차원)", description = "512차원 벡터 데이터가 있는 상품들의 스타일별 분포를 반환합니다.")
    @GetMapping("/style-count/512")
    public List<StyleCountDTO> getStyleCounts512() {
        return productService.countProductsByStyle512();
    }

    @Operation(summary = "스타일별 상품 통계 (768차원)", description = "768차원 벡터 데이터가 있는 상품들의 스타일별 분포를 반환합니다.")
    @GetMapping("/style-count/768")
    public List<StyleCountDTO> getStyleCounts768() {
        return productService.countProductsByStyle768();
    }

    @Operation(summary = "나인온스 상품 개수 조회", description = "InternalProducts 테이블의 총 상품 개수를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "1250")))
    @GetMapping("/internal-count")
    public long getInternalProductCount() {
        return productService.getInternalProductCount();
    }

    @Operation(summary = "네이버 상품 개수 조회", description = "NaverProduct 테이블의 총 상품 개수를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "45200")))
    @GetMapping("/naver-count")
    public long getNaverProductCount() {
        return productService.getNaverProductCount();
    }
}
