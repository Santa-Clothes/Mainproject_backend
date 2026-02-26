package com.kdt03.fashion_api.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveProductRequestDTO {
    private String naverProductId;
    private String styleName;
}
