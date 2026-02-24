package com.kdt03.fashion_api.domain.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesRankRespDTO {
    private String storeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<SalesLogDTO> products;
}
