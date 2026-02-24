package com.kdt03.fashion_api.domain.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastApiAnalysisDTO {
    private List<Double> embedding;

    @JsonProperty("analysisResult")
    private Map<String, Object> analysisResult;

    private String error;
    private String status;
}
