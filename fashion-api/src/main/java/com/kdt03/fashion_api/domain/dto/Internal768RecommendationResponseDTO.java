package com.kdt03.fashion_api.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Internal768RecommendationResponseDTO {
    private Integer dimension;
    private List<Internal768AnalysisDTO.StyleScoreDTO> styles;
    private List<SimilarProductDTO> internalProducts;
    private List<SimilarProductDTO> naverProducts;
}
