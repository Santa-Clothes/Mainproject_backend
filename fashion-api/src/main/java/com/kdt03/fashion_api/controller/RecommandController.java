package com.kdt03.fashion_api.controller;

import java.util.List;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommand")
public class RecommandController {

    private final RecommandService recommandService;

    @GetMapping("/demo/{productId}")
    public List<SimilarProductDTO> getDemoRecommendations(@PathVariable("productId") String productId) {
        return recommandService.getDemoRecommendations(productId);
    }

    @PostMapping("/demo/upload")
    public List<SimilarProductDTO> uploadDemoRecommendations(@RequestParam("file") MultipartFile file) {
        return recommandService.getUploadDemoRecommendations(file);
    }
}
