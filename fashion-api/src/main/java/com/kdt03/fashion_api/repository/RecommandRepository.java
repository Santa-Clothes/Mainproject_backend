package com.kdt03.fashion_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt03.fashion_api.domain.InternalProducts;
import com.kdt03.fashion_api.domain.dto.SimilarProductProjection;

public interface RecommandRepository extends JpaRepository<InternalProducts, String> {

       @Query(value = """
                     SELECT np.product_id as productId,
                            np.title as title,
                            np.price as price,
                            np.image_url as imageUrl,
                            np.product_link as productLink,
                            1 - (pv.embedding <=> npv.embedding) as similarityScore
                     FROM nineounce_product_vectors pv
                     JOIN naver_product_vectors npv ON true
                     JOIN naver_products np ON npv.product_id = np.product_id
                     WHERE pv.product_id = :productId
                     ORDER BY pv.embedding <=> npv.embedding
                     LIMIT 10
                     """, nativeQuery = true)
       List<SimilarProductProjection> findSimilarProducts(@Param("productId") String productId);

       @Query(value = """
                     SELECT target.product_id as productId,
                            p.product_name as title,
                            p.price as price,
                            p.image_url as imageUrl,
                            '' as productLink,
                            1 - (source.embedding <=> target.embedding) as similarityScore
                     FROM nineounce_product_vectors source
                     JOIN nineounce_product_vectors target ON target.product_id != source.product_id
                     JOIN nineounce_products p ON target.product_id = p.product_id
                     WHERE source.product_id = :productId
                     ORDER BY source.embedding <=> target.embedding
                     LIMIT 10
                     """, nativeQuery = true)
       List<SimilarProductProjection> findSimilarInternalProducts(@Param("productId") String productId);

       @Query(value = """
                     WITH random_samples AS (
                         SELECT ipv.product_id, ipv.embedding, ip.product_name, ip.price, ip.image_url
                         FROM nineounce_product_vectors ipv
                         JOIN nineounce_products ip ON ipv.product_id = ip.product_id
                         WHERE ipv.product_id != :productId AND ipv.embedding IS NOT NULL
                         ORDER BY RANDOM()
                         LIMIT 10
                     )
                     SELECT rs.product_id as productId,
                            rs.product_name as title,
                            rs.price as price,
                            rs.image_url as imageUrl,
                            '' as productLink,
                            1 - (p.embedding <=> rs.embedding) as similarityScore
                     FROM nineounce_product_vectors p
                     CROSS JOIN random_samples rs
                     WHERE p.product_id = :productId
                     ORDER BY similarityScore DESC
                     """, nativeQuery = true)
       List<SimilarProductProjection> findRandom10SimilarProducts(@Param("productId") String productId);

       @Query(value = """
                     WITH random_samples AS (
                         SELECT ipv.*, ip.product_name, ip.price, ip.image_url
                         FROM nineounce_product_vectors ipv
                         JOIN nineounce_products ip ON ipv.product_id = ip.product_id
                         WHERE ipv.embedding IS NOT NULL
                         ORDER BY RANDOM()
                         LIMIT 10
                     )
                     SELECT rs.product_id as productId,
                            rs.product_name as title,
                            rs.price as price,
                            rs.image_url as imageUrl,
                            '' as productLink,
                            1 - (cast(:embedding as vector) <=> rs.embedding) as similarityScore
                     FROM random_samples rs
                     ORDER BY similarityScore DESC
                     """, nativeQuery = true)
       List<SimilarProductProjection> findRandom10ByEmbedding(@Param("embedding") float[] embedding);

       @Query(value = """
                     SELECT p.product_id as productId,
                            p.product_name as title,
                            p.price as price,
                            p.image_url as imageUrl,
                            '' as productLink,
                            1 - (v.embedding <=> CAST(:embedding AS vector)) as similarityScore
                     FROM nineounce_products p
                     JOIN nineounce_product_vectors v ON p.product_id = v.product_id
                     ORDER BY v.embedding <=> CAST(:embedding AS vector)
                     LIMIT 10
                     """, nativeQuery = true)
       List<SimilarProductProjection> findTopSimilarInternalProducts(@Param("embedding") String embedding);

       @Query(value = """
                     SELECT p.product_id as productId,
                            p.product_name as title,
                            p.price as price,
                            p.image_url as imageUrl,
                            '' as productLink,
                            1 - (v.embedding <=> CAST(:embedding AS vector)) as similarityScore
                     FROM nineounce_products p
                     JOIN nineounce_product_vectors_768 v ON p.product_id = v.product_id
                     ORDER BY v.embedding <=> CAST(:embedding AS vector)
                     LIMIT 10
                     """, nativeQuery = true)
       List<SimilarProductProjection> findTopSimilarInternal768Products(@Param("embedding") String embedding);

       @Query(value = """
                     SELECT p.product_id as productId,
                            p.title as title,
                            p.price as price,
                            p.image_url as imageUrl,
                            p.product_link as productLink,
                            1 - (v.embedding <=> CAST(:embedding AS vector)) as similarityScore
                     FROM naver_products p
                     JOIN naver_product_vectors_768 v ON p.product_id = v.product_id
                     ORDER BY v.embedding <=> CAST(:embedding AS vector)
                     LIMIT 10
                     """, nativeQuery = true)
       List<SimilarProductProjection> findTopSimilarNaver768Products(@Param("embedding") String embedding);
}
