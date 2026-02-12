package com.kdt03.fashion_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kdt03.fashion_api.domain.SalesLog;

@Repository
public interface SalesLogRepository extends JpaRepository<SalesLog, Long> {
    @Query(value = """
            select to_char(sale_date, 'YYYY-MM') as month, ip.style as style, sum(sale_quantity) as total_quantity
            from sales_log sl join internal_products ip on sl.product_id = ip.product_id
            where extract(year from sale_date) = :year and ip.style is not null
            group by month, ip.style
            order by month, ip.style
            """, nativeQuery = true)
    List<Object[]> findMonthlySalesTrends(@Param("year") int year);

    @Query("SELECT DISTINCT YEAR(s.saleDate) FROM SalesLog s ORDER BY YEAR(s.saleDate)")
    List<Integer> findDistinctYears();
}
