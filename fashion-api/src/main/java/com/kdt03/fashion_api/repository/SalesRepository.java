package com.kdt03.fashion_api.repository;

import java.time.LocalDate;
import java.util.List;

import com.kdt03.fashion_api.domain.dto.SalesLogDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kdt03.fashion_api.domain.SalesLog;

@Repository
public interface SalesRepository extends JpaRepository<SalesLog, Long> {
        // 특정연도 스타일별 판매량 집계
        @Query(value = """
                        select to_char(sale_date, 'YYYY-MM') as month, st.style_name as style, sum(sale_quantity) as total_quantity
                        from sales_log sl
                        join nineounce_products ip on sl.product_id = ip.product_id
                        join nineounce_product_vectors_512 v on ip.product_id = v.product_id
                        join styles st on v.top1_style = st.style_id
                        where extract(year from sale_date) = :year and v.top1_style is not null
                        group by month, st.style_name
                        order by month, st.style_name
                        """, nativeQuery = true)
        List<Object[]> findMonthlySalesTrends(@Param("year") int year);

        // 특정연도 스타일별 판매량 집계(파라미터 없을 때)
        @Query(value = """
                        select extract(year from sale_date)::int
                        from sales_log
                        group by extract(year from sale_date)
                        order by extract(year from sale_date)
                        """, nativeQuery = true)
        List<Integer> findDistinctYears();

        // 전체 매장 판매량 집계
        @Query("SELECT new com.kdt03.fashion_api.domain.dto.SalesLogDTO(p.productId, p.productName, CAST(SUM(sl.saleQuantity) AS int)) "
                        + "FROM SalesLog sl "
                        + "JOIN InternalProducts p ON sl.productId = p.productId "
                        + "WHERE sl.saleDate BETWEEN :startDate AND :endDate "
                        + "GROUP BY p.productId, p.productName "
                        + "ORDER BY SUM(sl.saleQuantity) DESC")
        List<SalesLogDTO> findAllStores(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // 매장별 기간별 판매량 집계
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

        // 온라인 통합 판매량 집계
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
