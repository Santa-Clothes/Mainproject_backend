package com.kdt03.fashion_api.domain.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YearlyTrendDTO {
    private int year;
    private List<MonthlyTrendDTO> data;
}
