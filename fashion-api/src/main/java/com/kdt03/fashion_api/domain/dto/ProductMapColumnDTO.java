package com.kdt03.fashion_api.domain.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductMapColumnDTO {
    private List<String> productIds;
    private List<String> productNames;
    private List<String> styles;
    private List<Float> xCoords;
    private List<Float> yCoords;
}
