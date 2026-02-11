package com.kdt03.fashion_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StyleCountDTO {
    private String style;
    private Long count;
}
