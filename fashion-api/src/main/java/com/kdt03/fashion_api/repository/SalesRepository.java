package com.kdt03.fashion_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt03.fashion_api.domain.Sales;
import org.springframework.data.jpa.repository.Query;
import com.kdt03.fashion_api.domain.dto.SalesDTO;

public interface SalesRepository extends JpaRepository<Sales, Integer> {

    @Query("SELECT new com.kdt03.fashion_api.domain.dto.SalesDTO(p.productId, p.productName, s.saleQuantity, s.salePrice, s.saleRate) "
            +
            "FROM Sales s JOIN s.product p " +
            "ORDER BY s.saleQuantity DESC")
    List<SalesDTO> findTop10SalesDTO();
}
