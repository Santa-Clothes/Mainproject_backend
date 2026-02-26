package com.kdt03.fashion_api.client;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.kdt03.fashion_api.domain.dto.FastApiAnalysisDTO;

@HttpExchange
public interface FastApiClient {

    @PostExchange("/analyze")
    FastApiAnalysisDTO analyzeVector(@RequestPart("files") Resource file);

    @PostExchange("/upload-image")
    Map<String, Object> uploadImage(@RequestPart("files") Resource file);
}
