package com.kristalball.assetmanagement.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.Purchase;
import com.kristalball.assetmanagement.repository.PurchaseRepository;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public Purchase savePurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public void deletePurchase(Long id) {
        purchaseRepository.deleteById(id);
    }

    public List<Purchase> getPurchasesByFilters(
            LocalDate startDate,
            LocalDate endDate,
            Long baseId,
            Long categoryId) {

        if (startDate != null &&
            endDate != null &&
            baseId != null &&
            categoryId != null) {

            return purchaseRepository
                    .findByPurchaseDateBetweenAndBaseIdAndCategoryId(
                            startDate,
                            endDate,
                            baseId,
                            categoryId);
        }

        if (startDate != null &&
            endDate != null &&
            baseId != null) {

            return purchaseRepository
                    .findByPurchaseDateBetweenAndBaseId(
                            startDate,
                            endDate,
                            baseId);
        }

        if (startDate != null &&
            endDate != null &&
            categoryId != null) {

            return purchaseRepository
                    .findByPurchaseDateBetweenAndCategoryId(
                            startDate,
                            endDate,
                            categoryId);
        }

        if (startDate != null &&
            endDate != null) {

            return purchaseRepository
                    .findByPurchaseDateBetween(
                            startDate,
                            endDate);
        }

        if (baseId != null) {
            return purchaseRepository.findByBaseId(baseId);
        }

        if (categoryId != null) {
            return purchaseRepository.findByCategoryId(categoryId);
        }

        return purchaseRepository.findAll();
    }
}