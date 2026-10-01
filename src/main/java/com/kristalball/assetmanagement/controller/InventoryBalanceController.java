package com.kristalball.assetmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.InventoryBalance;
import com.kristalball.assetmanagement.service.AccessService;
import com.kristalball.assetmanagement.service.InventoryBalanceService;

@RestController
@RequestMapping("/api/inventory-balances")
public class InventoryBalanceController {

    private final InventoryBalanceService inventoryBalanceService;

    private final AccessService accessService;

    public InventoryBalanceController(
            InventoryBalanceService inventoryBalanceService,
            AccessService accessService) {

        this.inventoryBalanceService = inventoryBalanceService;
        this.accessService = accessService;
    }

    @GetMapping
    public List<InventoryBalance> getAll(
            @RequestParam(required = false) Long baseId) {

        if (accessService.isCommander() && baseId == null) {
            baseId = accessService.assignedBaseId();
        }

        accessService.requireBase(baseId);

        if (baseId == null) {
            return inventoryBalanceService
                    .getAllInventoryBalances();
        }

        return inventoryBalanceService
                .getInventoryBalancesByBase(baseId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryBalance> getById(
            @PathVariable Long id) {

        InventoryBalance inventoryBalance =
                inventoryBalanceService
                        .getInventoryBalanceById(id);

        if (inventoryBalance == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(
                inventoryBalance.getBase().getId());

        return ResponseEntity.ok(inventoryBalance);
    }

    @PostMapping
    public ResponseEntity<InventoryBalance> create(
            @RequestBody InventoryBalance inventoryBalance) {

        if (inventoryBalance.getBase() == null
                || inventoryBalance.getCategory() == null) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(
                inventoryBalance.getBase().getId());

        if (inventoryBalance.getOpeningBalance() == null) {
            inventoryBalance.setOpeningBalance(0);
        }

        InventoryBalance saved =
                inventoryBalanceService
                        .saveInventoryBalance(inventoryBalance);

        return ResponseEntity
                .status(201)
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryBalance> update(
            @PathVariable Long id,
            @RequestBody InventoryBalance inventoryBalance) {

        InventoryBalance existing =
                inventoryBalanceService
                        .getInventoryBalanceById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (inventoryBalance.getBase() == null
                || inventoryBalance.getCategory() == null) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(
                existing.getBase().getId());

        accessService.requireBase(
                inventoryBalance.getBase().getId());

        existing.setOpeningBalance(
                inventoryBalance.getOpeningBalance());

        existing.setBase(
                inventoryBalance.getBase());

        existing.setCategory(
                inventoryBalance.getCategory());

        InventoryBalance updated =
                inventoryBalanceService
                        .saveInventoryBalance(existing);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        InventoryBalance existing =
                inventoryBalanceService
                        .getInventoryBalanceById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(
                existing.getBase().getId());

        inventoryBalanceService
                .deleteInventoryBalance(id);

        return ResponseEntity.noContent().build();
    }
}