package com.kdt03.fashion_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kdt03.fashion_api.domain.Stores;

public interface StoreRepository extends JpaRepository<Stores, String> {
    @Query("SELECT s FROM Stores s WHERE s.storeId LIKE 'A%' ORDER BY s.storeId")
    List<Stores> findOfflineStores();
}
