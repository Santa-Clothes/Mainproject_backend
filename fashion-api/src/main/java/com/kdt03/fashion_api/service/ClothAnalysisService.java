package com.kdt03.fashion_api.service;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ClothAnalysisService {

    private final WebClient webClient;

    public ClothAnalysisService(WebClient.Builder webClientBuilder) {
        // FastAPI 서버 주소를 넣어줍니다.
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:8000").build();
    }

    public Mono<String> getFastApiHello() {
        return this.webClient.get()
                .uri("/")
                .retrieve()
                .bodyToMono(String.class);
    }
}