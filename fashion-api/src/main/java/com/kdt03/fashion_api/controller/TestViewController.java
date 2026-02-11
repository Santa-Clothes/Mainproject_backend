package com.kdt03.fashion_api.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Hidden // 테스트 페이지는 Swagger 문서에서 숨김
@Controller
public class TestViewController {

    @GetMapping("/test-page")
    public String uploadTestPage() {
        return "uploadTest";
    }

    @GetMapping("/product-list")
    public String DBTestPage() {
        return "productList";
    }

}