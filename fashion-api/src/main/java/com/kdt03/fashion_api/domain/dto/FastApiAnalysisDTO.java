package com.kdt03.fashion_api.domain.dto;

import java.util.List;

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
    @JsonProperty("vector")
    private List<Double> vector;

    @JsonProperty("dim")
    private Integer dim;

    // 필요 시 에러 전송을 위한 필드 유지
    private String error;
    private String status;
}
