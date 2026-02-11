package com.kdt03.fashion_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt03.fashion_api.domain.InternalProducts;

import org.springframework.data.jpa.repository.Query;
import com.kdt03.fashion_api.domain.dto.ProductDTO;

import java.util.List;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.kdt03.fashion_api.domain.dto.StyleCountDTO;

public interface ProductRepository extends JpaRepository<InternalProducts, String> {
        @Query("select new com.kdt03.fashion_api.domain.dto.ProductDTO(p.productId, p.productName, p.price, c.categoryName, p.imageUrl)"
                        + " from InternalProducts p left join p.category c "
                        + " where (:categoryName is null or c.categoryName = :categoryName)")
        List<ProductDTO> findAllProducts(@Param("categoryName") String categoryName);

        @Query("select new com.kdt03.fashion_api.domain.dto.ProductDTO(p.productId, p.productName, p.price, c.categoryName, p.imageUrl)"
                        + " from InternalProducts p left join p.category c where p.productId = :productId")
        Optional<ProductDTO> findProductById(@Param("productId") String productId);

        @Query("select new com.kdt03.fashion_api.domain.dto.ProductMapDTO(p.productId, p.productName, p.style, p.xCoord, p.yCoord) "
                        + "from InternalProducts p "
                        + "where p.style is not null and p.xCoord is not null and p.yCoord is not null")
        List<com.kdt03.fashion_api.domain.dto.ProductMapDTO> findAllProductMaps();

        @Query("select new com.kdt03.fashion_api.domain.dto.StyleCountDTO(p.style, count(p)) "
                        + "from InternalProducts p "
                        + "where p.style is not null "
                        + "group by p.style "
                        + "order by count(p) desc")
        List<StyleCountDTO> countProductsByStyle();
}
