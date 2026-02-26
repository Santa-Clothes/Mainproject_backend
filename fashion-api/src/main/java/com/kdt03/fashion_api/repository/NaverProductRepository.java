package com.kdt03.fashion_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt03.fashion_api.domain.NaverProducts;
import com.kdt03.fashion_api.domain.dto.SimilarProductProjection;

public interface NaverProductRepository extends JpaRepository<NaverProducts, String> {

    @Query(value = """
            SELECT p.product_id as productId, p.title, p.price, p.image_url as imageUrl, p.product_link as productLink,
                   (1 - (v.embedding <=> CAST(:embedding AS vector))) as similarityScore
            FROM naver_products p
            JOIN naver_product_vectors_512 v ON p.product_id = v.product_id
            ORDER BY v.embedding <=> CAST(:embedding AS vector)
            LIMIT 10
            """, nativeQuery = true)
    List<SimilarProductProjection> findTopSimilarProducts(@Param("embedding") String embedding);
}
