package com.kdt03.fashion_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.StoreDTO;
import com.kdt03.fashion_api.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/list")
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }
}
