package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.SaveProducts;
import com.kdt03.fashion_api.domain.dto.SaveProductRequestDTO;
import com.kdt03.fashion_api.domain.dto.SaveProductResponseDTO;
import com.kdt03.fashion_api.repository.NaverProductRepository;
import com.kdt03.fashion_api.repository.SaveProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaveProductService {

    private final SaveProductRepository saveProductRepository;
    private final NaverProductRepository naverProductRepository;
    private final com.kdt03.fashion_api.repository.MemberRepository memberRepository;

    // 관심 상품 등록
    public void addSaveProduct(String memberId, SaveProductRequestDTO dto) {
        // 회원 존재 여부 확인
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        if (saveProductRepository.existsByMemberIdAndNaverProductId(memberId, dto.getNaverProductId())) {
            throw new IllegalStateException("이미 관심 상품에 등록된 상품입니다.");
        }
        SaveProducts saveProduct = SaveProducts.builder()
                .memberId(memberId)
                .naverProductId(dto.getNaverProductId())
                .build();
        saveProductRepository.save(saveProduct);
    }

    // 내 관심 상품 목록 조회
    public List<SaveProductResponseDTO> getMySaveProducts(String memberId) {
        List<SaveProducts> saves = saveProductRepository.findByMemberId(memberId);
        if (saves.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // N+1 문제 해결을 위해 NaverProduct IDs 추출 후 한 번에 조회
        List<String> productIds = saves.stream()
                .map(SaveProducts::getNaverProductId)
                .collect(Collectors.toList());

        java.util.Map<String, com.kdt03.fashion_api.domain.NaverProducts> productMap = naverProductRepository
                .findAllById(productIds).stream()
                .collect(Collectors.toMap(com.kdt03.fashion_api.domain.NaverProducts::getProductId,
                        java.util.function.Function.identity()));

        return saves.stream()
                .map(save -> {
                    var naver = productMap.get(save.getNaverProductId());
                    if (naver == null)
                        return null;
                    return new SaveProductResponseDTO(
                            save.getSaveId(),
                            save.getNaverProductId(),
                            naver.getTitle(),
                            naver.getPrice(),
                            naver.getImageUrl(),
                            naver.getProductLink(),
                            save.getCreatedAt());
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    // 관심 상품 삭제
    public void deleteSaveProduct(Long saveId, String memberId) {
        SaveProducts saveProduct = saveProductRepository.findBySaveIdAndMemberId(saveId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관심 상품을 찾을 수 없습니다."));
        saveProductRepository.delete(saveProduct);
    }
}