package com.kristalball.assetmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.InventoryBalance;

public interface InventoryBalanceRepository
        extends JpaRepository<InventoryBalance, Long> {

    List<InventoryBalance> findByBaseId(Long baseId);
}
