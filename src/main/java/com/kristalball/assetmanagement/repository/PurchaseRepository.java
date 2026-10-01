package com.kristalball.assetmanagement.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kristalball.assetmanagement.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
    List<Purchase> findByPurchaseDateBetweenAndBaseId(LocalDate startDate, LocalDate endDate, Long baseId);
    List<Purchase> findByPurchaseDateBetweenAndCategoryId(LocalDate startDate, LocalDate endDate, Long categoryId);
    List<Purchase> findByPurchaseDateBetweenAndBaseIdAndCategoryId(LocalDate startDate, LocalDate endDate, Long baseId, Long categoryId);
    List<Purchase> findByBaseId(Long baseId);
    List<Purchase> findByCategoryId(Long categoryId);
}
