package com.kdt03.fashion_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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