package com.kdt03.fashion_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt03.fashion_api.domain.Products;

import org.springframework.data.jpa.repository.Query;
import com.kdt03.fashion_api.domain.dto.ProductDTO;
import java.util.List;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, String> {
        @Query("select new com.kdt03.fashion_api.domain.dto.ProductDTO(p.productId, p.productName, p.price, c.categoryName)"
                        + " from Products p left join p.category c")
        List<ProductDTO> findAllProducts();

        @Query("select new com.kdt03.fashion_api.domain.dto.ProductDTO(p.productId, p.productName, p.price, c.categoryName)"
                        + " from Products p left join p.category c where p.productId = :productId")
        Optional<ProductDTO> findProductById(@Param("productId") String productId);

        @Query("select new com.kdt03.fashion_api.domain.dto.ProductMapDTO(p.productId, p.productName, p.style, p.xCoord, p.yCoord) "
                        + "from Products p "
                        + "where p.style is not null and p.xCoord is not null and p.yCoord is not null")
        List<com.kdt03.fashion_api.domain.dto.ProductMapDTO> findAllProductMaps();
}
