package com.kdt03.fashion_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.dto.RecommendationResponseDTO;
import com.kdt03.fashion_api.domain.dto.SimilarProductDTO;
import com.kdt03.fashion_api.repository.RecommandRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommandService {
        private final RecommandRepository recRepo;

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

                return RecommendationResponseDTO.builder()
                                .naverProducts(naverResults)
                                .internalProducts(internalResults)
                                .build();
        }
}
