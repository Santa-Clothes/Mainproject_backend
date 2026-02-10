package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kdt03.fashion_api.domain.dto.SimilarProductDTO;
import com.kdt03.fashion_api.repository.RecommandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommandService {
    private final RecommandRepository recRepo;
    private final Random random = new Random();

    public List<SimilarProductDTO> recommand(String internalImageId) {
        return recRepo.findSimilarProducts(internalImageId).stream().map(p -> new SimilarProductDTO(
                p.getProductId(),
                p.getTitle(),
                p.getPrice(),
                p.getImageUrl(),
                p.getProductLink(),
                p.getSimilarityScore())).toList();
    }

    public List<SimilarProductDTO> getDemoRecommendations(String productId) {
        return recRepo.findRandom10SimilarProducts(productId).stream().map(p -> new SimilarProductDTO(
                p.getProductId(),
                p.getTitle(),
                p.getPrice(),
                p.getImageUrl(),
                p.getProductLink(),
                p.getSimilarityScore())).toList();
    }

    public List<SimilarProductDTO> getUploadDemoRecommendations(MultipartFile file) {
        float[] randomEmbedding = new float[2048];
        for (int i = 0; i < 2048; i++) {
            randomEmbedding[i] = random.nextFloat();
        }

        return recRepo.findRandom10ByEmbedding(randomEmbedding).stream().map(p -> new SimilarProductDTO(
                p.getProductId(),
                p.getTitle(),
                p.getPrice(),
                p.getImageUrl(),
                p.getProductLink(),
                p.getSimilarityScore())).toList();
    }
}