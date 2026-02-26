package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt03.fashion_api.domain.SaveProducts;
import com.kdt03.fashion_api.domain.dto.SaveProductRequestDTO;
import com.kdt03.fashion_api.domain.dto.SaveProductResponseDTO;
import com.kdt03.fashion_api.repository.NaverProductRepository;
import com.kdt03.fashion_api.repository.SaveProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveProductService {

    private final SaveProductRepository saveProductRepository;
    private final NaverProductRepository naverProductRepository;
    private final com.kdt03.fashion_api.repository.MemberRepository memberRepository;
    private final com.kdt03.fashion_api.repository.NaverProductVectors512Repository naverVectors512Repository;
    private final com.kdt03.fashion_api.repository.NaverProductVectors768Repository naverVectors768Repository;

    private final com.kdt03.fashion_api.repository.StylesRepository stylesRepository;

    // 관심 상품 등록
    public void addSaveProduct(String memberId, SaveProductRequestDTO dto) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        if (saveProductRepository.existsByMemberIdAndNaverProductId(memberId, dto.getNaverProductId())) {
            throw new IllegalStateException("이미 관심 상품에 등록된 상품입니다.");
        }

        com.kdt03.fashion_api.domain.Styles style = null;
        if (dto.getStyleName() != null && !dto.getStyleName().isEmpty()) {
            style = stylesRepository.findByStyleName(dto.getStyleName()).orElse(null);
        }

        SaveProducts saveProduct = SaveProducts.builder()
                .memberId(memberId)
                .naverProductId(dto.getNaverProductId())
                .style(style)
                .build();
        saveProductRepository.save(saveProduct);
    }

    // 내 관심 상품 목록 조회
    public List<SaveProductResponseDTO> getMySaveProducts(String memberId) {
        List<SaveProducts> saves = saveProductRepository.findByMemberId(memberId);
        if (saves.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<String> productIds = saves.stream()
                .map(SaveProducts::getNaverProductId)
                .collect(Collectors.toList());

        // 1. 네이버 상품 정보 조회
        java.util.Map<String, com.kdt03.fashion_api.domain.NaverProducts> productMap = naverProductRepository
                .findAllById(productIds).stream()
                .collect(Collectors.toMap(com.kdt03.fashion_api.domain.NaverProducts::getProductId,
                        java.util.function.Function.identity()));

        // 2. 512차원 스타일 정보 조회
        java.util.Map<String, com.kdt03.fashion_api.domain.NaverProductVectors512> vectors512Map = naverVectors512Repository
                .findAllById(productIds).stream()
                .collect(Collectors.toMap(com.kdt03.fashion_api.domain.NaverProductVectors512::getProductId,
                        java.util.function.Function.identity()));

        // 3. 768차원 스타일 정보 조회
        java.util.Map<String, com.kdt03.fashion_api.domain.NaverProductVectors768> vectors768Map = naverVectors768Repository
                .findAllById(productIds).stream()
                .collect(Collectors.toMap(com.kdt03.fashion_api.domain.NaverProductVectors768::getProductId,
                        java.util.function.Function.identity()));

        return saves.stream()
                .map(save -> {
                    var naver = productMap.get(save.getNaverProductId());
                    if (naver == null)
                        return null;

                    var v512 = vectors512Map.get(save.getNaverProductId());
                    var v768 = vectors768Map.get(save.getNaverProductId());

                    return SaveProductResponseDTO.builder()
                            .saveId(save.getSaveId())
                            .naverProductId(save.getNaverProductId())
                            .title(naver.getTitle())
                            .price(naver.getPrice())
                            .imageUrl(naver.getImageUrl())
                            .productLink(naver.getProductLink())
                            .createdAt(save.getCreatedAt())
                            .styleTop1_512(
                                    v512 != null && v512.getTop1Style() != null ? v512.getTop1Style().getStyleName()
                                            : null)
                            .styleScore1_512(v512 != null ? v512.getTop1Score() : null)
                            .styleTop1_768(
                                    v768 != null && v768.getStyleTop1() != null ? v768.getStyleTop1().getStyleName()
                                            : null)
                            .styleScore1_768(v768 != null ? v768.getStyleScore1() : null)
                            .build();
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    // 관심 상품 다건 삭제 - naver_product_id 배열 + JWT에서 뽑은 memberId 기준으로 삭제
    @Transactional
    public void deleteSaveProducts(List<String> naverProductIds, String memberId) {
        log.info("[DeleteSaveProducts] 요청 - memberId: {}, naverProductIds: {}", memberId, naverProductIds);

        // member_id = memberId AND naver_product_id IN (naverProductIds) 조건으로 조회
        List<SaveProducts> targets = saveProductRepository
                .findByMemberIdAndNaverProductIdIn(memberId, naverProductIds);

        log.info("[DeleteSaveProducts] 조회 결과: {}건 발견 (요청 {}건 중)", targets.size(), naverProductIds.size());

        if (!targets.isEmpty()) {
            saveProductRepository.deleteAll(targets);
            log.info("[DeleteSaveProducts] DB 삭제 완료");
        } else {
            log.warn("[DeleteSaveProducts] 삭제 대상 없음 - memberId 또는 naverProductId 불일치 확인 필요");
        }
    }
}