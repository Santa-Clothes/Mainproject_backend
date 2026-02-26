package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kdt03.fashion_api.domain.dto.Internal768AnalysisDTO;
import com.kdt03.fashion_api.domain.dto.Internal768RecommendationResponseDTO;
import com.kdt03.fashion_api.domain.dto.RecommendationResponseDTO;
import com.kdt03.fashion_api.domain.dto.SimilarProductDTO;
import com.kdt03.fashion_api.repository.RecommandRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecommandService {
        private final RecommandRepository recRepo;
        private final com.kdt03.fashion_api.client.InternalFastApiClient internalFastApiClient;

        public RecommandService(RecommandRepository recRepo,
                        com.kdt03.fashion_api.client.InternalFastApiClient internalFastApiClient) {
                this.recRepo = recRepo;
                this.internalFastApiClient = internalFastApiClient;
        }

        public RecommendationResponseDTO recommand(String productId) {
                log.info("Finding similar products for Nineounce product: {}", productId);

                // 1. 네이버 유사 상품 검색
                List<SimilarProductDTO> naverResults = recRepo.findSimilarProducts(productId).stream()
                                .map(p -> new SimilarProductDTO(
                                                p.getProductId(),
                                                p.getTitle(),
                                                p.getPrice(),
                                                p.getImageUrl(),
                                                p.getProductLink(),
                                                p.getSimilarityScore()))
                                .toList();

                // 2. 내부 유사 상품 검색
                List<SimilarProductDTO> internalResults = recRepo.findSimilarInternalProducts(productId).stream()
                                .map(p -> new SimilarProductDTO(
                                                p.getProductId(),
                                                p.getTitle(),
                                                p.getPrice(),
                                                p.getImageUrl(),
                                                p.getProductLink(),
                                                p.getSimilarityScore()))
                                .toList();

                log.info("Found {} naver and {} internal similar products.", naverResults.size(),
                                internalResults.size());

                if (!naverResults.isEmpty() || !internalResults.isEmpty()) {
                        log.info("최상위 네이버 추천: {}, 최상위 내부 추천: {}",
                                        naverResults.isEmpty() ? "없음" : naverResults.getFirst().getTitle(),
                                        internalResults.isEmpty() ? "없음" : internalResults.getFirst().getTitle());
                }

                return RecommendationResponseDTO.builder()
                                .naverProducts(naverResults)
                                .internalProducts(internalResults)
                                .build();
        }

        public RecommendationResponseDTO recommand768(String productId) {
                log.info("Finding 768-dim similar products for Nineounce product: {}", productId);

                // 1. 네이버 유사 상품 검색 (768차원)
                List<SimilarProductDTO> naverResults = recRepo.findSimilar768Products(productId).stream()
                                .map(p -> new SimilarProductDTO(
                                                p.getProductId(),
                                                p.getTitle(),
                                                p.getPrice(),
                                                p.getImageUrl(),
                                                p.getProductLink(),
                                                p.getSimilarityScore()))
                                .toList();

                // 2. 내부 유사 상품 검색 (768차원)
                List<SimilarProductDTO> internalResults = recRepo.findSimilarInternal768Products(productId).stream()
                                .map(p -> new SimilarProductDTO(
                                                p.getProductId(),
                                                p.getTitle(),
                                                p.getPrice(),
                                                p.getImageUrl(),
                                                p.getProductLink(),
                                                p.getSimilarityScore()))
                                .toList();

                log.info("Found {} naver and {} internal similar products (768-dim).", naverResults.size(),
                                internalResults.size());

                return RecommendationResponseDTO.builder()
                                .naverProducts(naverResults)
                                .internalProducts(internalResults)
                                .build();
        }

        @Cacheable(value = "recommendations", key = "#file.originalFilename + #file.size", condition = "#file != null")
        public Internal768RecommendationResponseDTO analyzeInternal768(MultipartFile file) {
                log.info("Processing 768 analysis for file: {}", file.getOriginalFilename());

                try {
                        Internal768AnalysisDTO fastApiResponse = internalFastApiClient
                                        .analyzeInternal768(file.getResource());

                        if (fastApiResponse != null && fastApiResponse.getEmbedding() != null) {
                                List<Double> embeddingList = fastApiResponse.getEmbedding();
                                String vectorString = embeddingList.stream()
                                                .map(String::valueOf)
                                                .collect(Collectors.joining(",", "[", "]"));

                                java.util.concurrent.CompletableFuture<List<SimilarProductDTO>> internalTask = java.util.concurrent.CompletableFuture
                                                .supplyAsync(() -> recRepo
                                                                .findTopSimilarInternal768Products(vectorString)
                                                                .stream()
                                                                .map(p -> new SimilarProductDTO(
                                                                                p.getProductId(),
                                                                                p.getTitle(),
                                                                                p.getPrice(),
                                                                                p.getImageUrl(),
                                                                                p.getProductLink(),
                                                                                p.getSimilarityScore()))
                                                                .collect(Collectors.toList()));

                                java.util.concurrent.CompletableFuture<List<SimilarProductDTO>> naverTask = java.util.concurrent.CompletableFuture
                                                .supplyAsync(() -> recRepo.findTopSimilarNaver768Products(vectorString)
                                                                .stream()
                                                                .map(p -> new SimilarProductDTO(
                                                                                p.getProductId(),
                                                                                p.getTitle(),
                                                                                p.getPrice(),
                                                                                p.getImageUrl(),
                                                                                p.getProductLink(),
                                                                                p.getSimilarityScore()))
                                                                .collect(Collectors.toList()));

                                java.util.concurrent.CompletableFuture.allOf(internalTask, naverTask).join();
                                List<SimilarProductDTO> internalProducts = internalTask.get();
                                List<SimilarProductDTO> naverProducts = naverTask.get();

                                return Internal768RecommendationResponseDTO.builder()
                                                .dimension(fastApiResponse.getDimension())
                                                .styles(fastApiResponse.getStyles())
                                                .internalProducts(internalProducts)
                                                .naverProducts(naverProducts)
                                                .build();
                        }
                } catch (Exception e) {
                        log.error("Error during internal 768 analysis: {}", e.getMessage(), e);
                        throw new RuntimeException("768 분석 중 오류 발생: " + e.getMessage());
                }
                return null;
        }
}
