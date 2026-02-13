package com.kdt03.fashion_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt03.fashion_api.domain.InternalProducts512;
import com.kdt03.fashion_api.domain.dto.Product512DTO;

import java.util.List;

public interface InternalProducts512Repository extends JpaRepository<InternalProducts512, String> {

    // 전체 리스트 조회 (DTO 프로젝션)
    @Query("SELECT new com.kdt03.fashion_api.domain.dto.Product512DTO(p.productId, p.imageUrl, null) " +
            "FROM InternalProducts512 p")
    List<Product512DTO> findAllProducts512();

    // 벡터 유사도 기반 Top 20 조회 (네이티브 쿼리)
    @Query(value = "SELECT " +
            "p.product_id as productId, " +
            "p.image_url as imageUrl, " +
            "(1 - (p.embedding <=> target.embedding)) as similarity " +
            "FROM internal_products_512 p " +
            "CROSS JOIN ( " +
            "    SELECT embedding FROM internal_products_512 WHERE product_id = :productId " +
            ") target " +
            "WHERE p.product_id != :productId " +
            "ORDER BY p.embedding <=> target.embedding " +
            "LIMIT 20", nativeQuery = true)
    List<Object[]> findTop20SimilarProducts(@Param("productId") String productId);
}
