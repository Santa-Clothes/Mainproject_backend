package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt03.fashion_api.domain.dto.SalesDTO;
import com.kdt03.fashion_api.repository.SalesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;

    @Transactional(readOnly = true)
    public List<SalesDTO> getTop10BestSellingProducts() {
        return salesRepository.findTop10SalesDTO().stream()
                .limit(10)
                .collect(Collectors.toList());
    }
}
