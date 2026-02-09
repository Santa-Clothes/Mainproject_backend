package com.kdt03.fashion_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.dto.SimilarProductDTO;
import com.kdt03.fashion_api.repository.RecommandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommandService {
    private final RecommandRepository recRepo;

    public List<SimilarProductDTO> recommand(String internalImageId) {
        return  recRepo.findSimilarProducts(internalImageId).stream().map(p -> new SimilarProductDTO(
            p.getProductId(),
            p.getTitle(),
            p.getPrice(),
            p.getImageUrl(),
            p.getProductLink(),
            p.getSimilarityScore()
        )).toList();
    }
}