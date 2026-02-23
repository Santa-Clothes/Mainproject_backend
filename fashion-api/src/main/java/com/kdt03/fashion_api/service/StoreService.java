package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.dto.StoreDTO;
import com.kdt03.fashion_api.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepo;

    public List<StoreDTO> getAllStores() {
        return storeRepo.findAll().stream()
                .map(store -> new StoreDTO(store.getStoreId(), store.getStoreName()))
                .collect(Collectors.toList());
    }
}
