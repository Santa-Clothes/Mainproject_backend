package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt03.fashion_api.domain.dto.SalesDTO;
import com.kdt03.fashion_api.domain.dto.SalesLogDTO;
import com.kdt03.fashion_api.domain.dto.SalesRankRespDTO;

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

        if (storeId != null && (storeId.equalsIgnoreCase("online") || storeId.toUpperCase().startsWith("S"))) {
            return salesLogRepository.findTop10OnlineSalesDTO(startDate, endDate, storeId).stream()
                    .limit(10)
                    .collect(Collectors.toList());
        }

        return salesLogRepository.findBestSellingProducts(startDate, endDate, storeId).stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    // 현지
    public SalesRankRespDTO getSalesByStore(LocalDate startDate, LocalDate endDate, String storeId) {
        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.now();

        List<SalesLogDTO> products;
        if (storeId.equalsIgnoreCase("online")) {
            products = salesLogRepository.findByOnline(startDate, endDate)
                    .stream().limit(5).collect(Collectors.toList());
        } else {
            products = salesLogRepository.findByStore(startDate, endDate, storeId)
                    .stream().limit(5).collect(Collectors.toList());
        }

        return SalesRankRespDTO.builder()
                .storeId(storeId)
                .startDate(startDate)
                .endDate(endDate)
                .products(products)
                .build();
    }
}
