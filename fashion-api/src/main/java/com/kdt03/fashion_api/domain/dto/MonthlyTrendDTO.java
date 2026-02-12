package com.kdt03.fashion_api.domain.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MonthlyTrendDTO {
    private int month;
    private Map<String, Integer> styles;
}
