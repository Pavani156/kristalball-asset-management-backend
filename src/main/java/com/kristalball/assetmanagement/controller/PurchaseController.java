package com.kristalball.assetmanagement.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.Purchase;
import com.kristalball.assetmanagement.service.AccessService;
import com.kristalball.assetmanagement.service.AuditLogService;
import com.kristalball.assetmanagement.service.PurchaseService;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final AuditLogService auditLogService;
    private final AccessService accessService;

    public PurchaseController(
            PurchaseService purchaseService,
            AuditLogService auditLogService,
            AccessService accessService) {

        this.purchaseService = purchaseService;
        this.auditLogService = auditLogService;
        this.accessService = accessService;
    }

    @GetMapping
    public List<Purchase> getAllPurchases(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long categoryId) {

        if (accessService.isCommander() && baseId == null) {
            baseId = accessService.assignedBaseId();
        }
        accessService.requireBase(baseId);

        return purchaseService.getPurchasesByFilters(startDate, endDate, baseId, categoryId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(
            @PathVariable Long id) {

        Purchase purchase =
                purchaseService.getPurchaseById(id);

        if (purchase == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(
                purchase.getBase().getId());

        return ResponseEntity.ok(purchase);
    }

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(
            @RequestBody Purchase purchase) {

        if (purchase.getBase() == null
                || purchase.getCategory() == null
                || purchase.getQuantity() == null
                || purchase.getQuantity() <= 0
                || purchase.getPurchaseDate() == null) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(
                purchase.getBase().getId());

        Purchase saved =
                purchaseService.savePurchase(purchase);

        auditLogService.log(
                "CREATE",
                "PURCHASE",
                saved.getId(),
                currentUsername(),
                "Purchase created, quantity="
                        + saved.getQuantity()
                        + ", supplier="
                        + saved.getSupplier());

        return ResponseEntity
                .status(201)
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(
            @PathVariable Long id,
            @RequestBody Purchase purchase) {

        Purchase existing =
                purchaseService.getPurchaseById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (purchase.getBase() == null
                || purchase.getCategory() == null
                || purchase.getQuantity() == null
                || purchase.getQuantity() <= 0
                || purchase.getPurchaseDate() == null) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(
                existing.getBase().getId());

        accessService.requireBase(
                purchase.getBase().getId());

        existing.setPurchaseDate(
                purchase.getPurchaseDate());

        existing.setQuantity(
                purchase.getQuantity());

        existing.setSupplier(
                purchase.getSupplier());

        existing.setCategory(
                purchase.getCategory());

        existing.setBase(
                purchase.getBase());

        Purchase updated =
                purchaseService.savePurchase(existing);

        auditLogService.log(
                "UPDATE",
                "PURCHASE",
                id,
                currentUsername(),
                "Purchase updated");

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(
            @PathVariable Long id) {

        Purchase existing =
                purchaseService.getPurchaseById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(
                existing.getBase().getId());

        purchaseService.deletePurchase(id);

        auditLogService.log(
                "DELETE",
                "PURCHASE",
                id,
                currentUsername(),
                "Purchase deleted");

        return ResponseEntity.noContent().build();
    }

    private String currentUsername() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}