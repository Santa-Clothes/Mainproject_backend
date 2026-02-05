package com.kdt03.fashion_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.service.ClothAnalysisService;

import reactor.core.publisher.Mono;

@RestController
public class TestController {

    // @GetMapping("/test-thread")
    // public String test() {

    //     return Thread.currentThread().toString();
    // }



    private final ClothAnalysisService clothAnalysisService;

    public TestController(ClothAnalysisService clothAnalysisService) {
        this.clothAnalysisService = clothAnalysisService;
    }

    @GetMapping("/test-fastapi")
    public Mono<String> test() {
        return clothAnalysisService.getFastApiHello();
    }
}