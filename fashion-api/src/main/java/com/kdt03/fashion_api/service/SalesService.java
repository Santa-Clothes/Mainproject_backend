package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt03.fashion_api.domain.dto.SalesDTO;
import java.time.LocalDate;
import com.kdt03.fashion_api.repository.SalesLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesLogRepository salesLogRepository;

    @Transactional(readOnly = true)
    public List<SalesDTO> getTop10BestSellingProducts(LocalDate startDate, LocalDate endDate, String storeId) {
        if (startDate == null)
            startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null)
            endDate = LocalDate.now();
        if (storeId.startsWith("s")) {
            return salesLogRepository.findTop10OnlineSalesDTO(startDate, endDate, storeId).stream()
            .limit(10)
            .collect(Collectors.toList());
        }

        return salesLogRepository.findBestSellingProducts(startDate, endDate, storeId).stream()
                .limit(10)
                .collect(Collectors.toList());
    }
}
