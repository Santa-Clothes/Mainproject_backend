package com.kdt03.fashion_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.repository.ProductRepository;
import com.kdt03.fashion_api.domain.dto.ProductDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepo;

    public List<ProductDTO> findAllProducts() {
        return productRepo.findAllProducts();
    }
}