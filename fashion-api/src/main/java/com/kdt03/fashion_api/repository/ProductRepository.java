package com.kdt03.fashion_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt03.fashion_api.domain.InternalProducts;
import com.kdt03.fashion_api.domain.dto.ProductDTO;

public interface ProductRepository extends JpaRepository<InternalProducts, String> {
        @Query("select new com.kdt03.fashion_api.domain.dto.ProductDTO(p.productId, p.productName, p.price, c.categoryName, p.imageUrl) "
                        + "from InternalProducts p "
                        + "left join p.category c "
                        + "left join SalesLog s on s.productId = p.productId "
                        + "and s.saleDate >= :oneYearAgo "
                        + "where (:categoryName is null or c.categoryName = :categoryName) "
                        + "group by p.productId, p.productName, p.price, c.categoryName, p.imageUrl "
                        + "order by coalesce(sum(s.saleQuantity), 0) desc")
        List<ProductDTO> findAllProducts(@Param("categoryName") String categoryName,
                        @Param("oneYearAgo") java.time.LocalDate oneYearAgo);

        @Query("select new com.kdt03.fashion_api.domain.dto.ProductDTO(p.productId, p.productName, p.price, c.categoryName, p.imageUrl)"
                        + " from InternalProducts p left join p.category c where p.productId = :productId")
        Optional<ProductDTO> findProductById(@Param("productId") String productId);

        @Query("select new com.kdt03.fashion_api.domain.dto.ProductMapDTO(p.productId, p.productName, s.styleName, v.x, v.y, v.z) "
                        + "from InternalProducts p "
                        + "join NineounceXyz512 v on v.productId = p.productId "
                        + "left join v.style s "
                        + "where v.x is not null and v.y is not null and v.z is not null")
        List<com.kdt03.fashion_api.domain.dto.ProductMapDTO> findAllProductMaps512();

        @Query("select new com.kdt03.fashion_api.domain.dto.ProductMapDTO(p.productId, p.productName, s.styleName, v.x, v.y, v.z) "
                        + "from InternalProducts p "
                        + "join NineounceXyz768 v on v.productId = p.productId "
                        + "left join v.style s "
                        + "where v.x is not null and v.y is not null and v.z is not null")
        List<com.kdt03.fashion_api.domain.dto.ProductMapDTO> findAllProductMaps768();

        @Query(value = "SELECT s.style_name as styleName, COUNT(p.product_id) as count " +
                        "FROM nineounce_products p " +
                        "JOIN nineounce_product_vectors_512 v ON p.product_id = v.product_id " +
                        "JOIN styles s ON v.top1_style = s.style_id " +
                        "GROUP BY s.style_name " +
                        "ORDER BY count DESC", nativeQuery = true)
        List<java.util.Map<String, Object>> countProductsByStyle512();

        @Query(value = "SELECT s.style_name as styleName, COUNT(p.product_id) as count " +
                        "FROM nineounce_products p " +
                        "JOIN nineounce_product_vectors_768 v ON p.product_id = v.product_id " +
                        "JOIN styles s ON v.style_top1 = s.style_id " +
                        "GROUP BY s.style_name " +
                        "ORDER BY count DESC", nativeQuery = true)
        List<java.util.Map<String, Object>> countProductsByStyle768();
}
