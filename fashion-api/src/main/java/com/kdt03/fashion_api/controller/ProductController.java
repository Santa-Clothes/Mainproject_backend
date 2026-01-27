package com.kdt03.fashion_api.controller;

import com.kdt03.fashion_api.domain.dto.ProductDTO;
import com.kdt03.fashion_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/list")
    public List<ProductDTO> getProducts() {
        return productService.findAllProducts().stream().limit(10).toList();
    }
}