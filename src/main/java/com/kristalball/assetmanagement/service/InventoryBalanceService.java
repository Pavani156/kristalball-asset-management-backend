package com.kristalball.assetmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.InventoryBalance;
import com.kristalball.assetmanagement.repository.InventoryBalanceRepository;

@Service
public class InventoryBalanceService {

    private final InventoryBalanceRepository repository;

    public InventoryBalanceService(
            InventoryBalanceRepository repository) {

        this.repository = repository;
    }

    public List<InventoryBalance> getAllInventoryBalances() {
        return repository.findAll();
    }

    public List<InventoryBalance> getInventoryBalancesByBase(
            Long baseId) {

        return repository.findByBaseId(baseId);
    }

    public InventoryBalance getInventoryBalanceById(
            Long id) {

        return repository.findById(id).orElse(null);
    }

    public InventoryBalance saveInventoryBalance(
            InventoryBalance inventoryBalance) {

        return repository.save(inventoryBalance);
    }

    public void deleteInventoryBalance(Long id) {
        repository.deleteById(id);
    }
}