package com.kdt03.fashion_api.domain.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResponseDTO {
    private Map<String, Object> analysisResult;
    private List<SimilarProductDTO> similarProducts;
}
