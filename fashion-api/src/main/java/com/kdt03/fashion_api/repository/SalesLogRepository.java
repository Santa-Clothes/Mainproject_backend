package com.kdt03.fashion_api.repository;

import java.time.LocalDate;
import java.util.List;

import com.kdt03.fashion_api.domain.dto.SalesDTO;
import com.kdt03.fashion_api.domain.dto.SalesLogDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kdt03.fashion_api.domain.SalesLog;

@Repository
public interface SalesLogRepository extends JpaRepository<SalesLog, Long> {
        @Query(value = """
                        select to_char(sale_date, 'YYYY-MM') as month, st.style_name as style, sum(sale_quantity) as total_quantity
                        from sales_log sl
                        join nineounce_products ip on sl.product_id = ip.product_id
                        join styles st on ip.style_id = st.style_id
                        where extract(year from sale_date) = :year and ip.style_id is not null
                        group by month, st.style_name
                        order by month, st.style_name
                        """, nativeQuery = true)
        List<Object[]> findMonthlySalesTrends(@Param("year") int year);

        @Query(value = """
                        select extract(year from sale_date)::int
                        from sales_log
                        group by extract(year from sale_date)
                        order by extract(year from sale_date)
                        """, nativeQuery = true)
        List<Integer> findDistinctYears();

        @Query("SELECT new com.kdt03.fashion_api.domain.dto.SalesDTO(p.productId, p.productName, CAST(SUM(sl.saleQuantity) AS int), p.price) "
                        +
                        "FROM SalesLog sl " +
                        "JOIN InternalProducts p ON sl.productId = p.productId " +
                        "WHERE sl.saleDate BETWEEN :startDate AND :endDate " +
                        "AND (:storeId IS NULL OR sl.store.storeId = :storeId) " +
                        "GROUP BY p.productId, p.productName, p.price " +
                        "ORDER BY SUM(sl.saleQuantity) DESC")
        List<SalesDTO> findBestSellingProducts(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("storeId") String storeId);

        @Query("SELECT new com.kdt03.fashion_api.domain.dto.SalesDTO(p.productId, p.productName, CAST(SUM(sl.saleQuantity) AS int), p.price) "
                        +
                        "FROM SalesLog sl " +
                        "JOIN InternalProducts p ON sl.productId = p.productId " +
                        "WHERE sl.saleDate BETWEEN :startDate AND :endDate " +
                        "AND  sl.store.storeId LIKE 'S%' " +
                        "GROUP BY p.productId, p.productName, p.price " +
                        "ORDER BY SUM(sl.saleQuantity) DESC")
        List<SalesDTO> findTop10OnlineSalesDTO(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("storeId") String storeId);

        
        // 현지
        @Query("SELECT new com.kdt03.fashion_api.domain.dto.SalesLogDTO(p.productId, p.productName, CAST(SUM(sl.saleQuantity) AS int)) "
                        + "FROM SalesLog sl "
                        + "JOIN InternalProducts p ON sl.productId = p.productId "
                        + "WHERE sl.saleDate BETWEEN :startDate AND :endDate "
                        + "AND sl.store.storeId = :storeId "
                        + "GROUP BY p.productId, p.productName "
                        + "ORDER BY SUM(sl.saleQuantity) DESC")
        List<SalesLogDTO> findByStore(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("storeId") String storeId);

        @Query("SELECT new com.kdt03.fashion_api.domain.dto.SalesLogDTO(p.productId, p.productName, CAST(SUM(sl.saleQuantity) AS int)) "
                        + "FROM SalesLog sl "
                        + "JOIN InternalProducts p ON sl.productId = p.productId "
                        + "WHERE sl.saleDate BETWEEN :startDate AND :endDate "
                        + "AND sl.store.storeId LIKE 'S%' "
                        + "GROUP BY p.productId, p.productName "
                        + "ORDER BY SUM(sl.saleQuantity) DESC")
        List<SalesLogDTO> findByOnline(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}
