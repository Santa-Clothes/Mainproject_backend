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
    @JsonProperty("latent_dim")
    private Integer latentDim;

    @JsonProperty("device")
    private String device;

    @JsonProperty("total_latency_ms")
    private Double totalLatencyMs;

    @JsonProperty("results")
    private List<ResultDTO> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDTO {
        @JsonProperty("file")
        private String file;

        @JsonProperty("top1_score")
        private Double top1Score;

        @JsonProperty("unknown")
        private Boolean unknown;

        @JsonProperty("topk")
        private List<Map<String, Object>> topk;

        @JsonProperty("latent_vector")
        private List<Double> latentVector;
    }

    // 하위 호환성 또는 에러 처리를 위한 필드
    private String error;
    private String status;
}
