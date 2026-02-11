package com.kdt03.fashion_api.controller;

import com.kdt03.fashion_api.domain.dto.ProductDTO;
import com.kdt03.fashion_api.domain.dto.ProductMapColumnDTO;
import com.kdt03.fashion_api.domain.dto.StyleCountDTO;
import com.kdt03.fashion_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/list")
    public List<ProductDTO> getProducts(@RequestParam(value = "categoryName", required = false) String categoryName) {
        return productService.findAllProducts(categoryName);
    }

    @GetMapping("/detail")
    public ResponseEntity<ProductDTO> getProduct(@RequestParam("productId") String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/map")
    public ProductMapColumnDTO getProductMap() {
        return productService.getProductMapData();
    }

    @GetMapping("/style-count")
    public List<StyleCountDTO> getStyleCounts() {
        return productService.countProductsByStyle();
    }
}
