package com.kristalball.assetmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByFromBaseId(Long fromBaseId);

    List<Transfer> findByToBaseId(Long toBaseId);

    List<Transfer> findByAssetId(Long assetId);

    List<Transfer> findByStatus(String status);
}