package com.kdt03.fashion_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt03.fashion_api.domain.dto.SalesLogDTO;
import com.kdt03.fashion_api.domain.dto.SalesRankRespDTO;

import java.time.LocalDate;
import com.kdt03.fashion_api.repository.SalesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final SalesRepository salesRepository;

    @Transactional(readOnly = true)
    public SalesRankRespDTO getSalesByStore(LocalDate startDate, LocalDate endDate, String storeId) {
        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.now();

        List<SalesLogDTO> products;
        if (storeId == null) {
            products = salesRepository.findAllStores(startDate, endDate)
                    .stream().limit(5).collect(Collectors.toList());
        } else if (storeId.equalsIgnoreCase("online")) {
            products = salesRepository.findByOnline(startDate, endDate)
                    .stream().limit(5).collect(Collectors.toList());
        } else {
            products = salesRepository.findByStore(startDate, endDate, storeId)
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
