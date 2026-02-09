package com.kdt03.fashion_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.SimilarProductDTO;
import com.kdt03.fashion_api.service.RecommandService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommand")
public class RecommandController {
    private final RecommandService recService;

    @GetMapping("/{productId}")
    public List<SimilarProductDTO> recommand(@PathVariable String productId) {
        return recService.recommand(productId);
    }
}
