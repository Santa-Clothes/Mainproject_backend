package com.kdt03.fashion_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt03.fashion_api.domain.Products;

import org.springframework.data.jpa.repository.Query;
import com.kdt03.fashion_api.domain.dto.ProductDTO;
import java.util.List;


public interface ProductRepository extends JpaRepository<Products, String> {
    @Query("select new com.kdt03.fashion_api.domain.dto.ProductDTO(p.productId, p.productName, p.price, c.categoryName)"
       + " from Products p left join p.category c")
    List<ProductDTO> findAllProducts();
}
