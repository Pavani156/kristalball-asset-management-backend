package com.kristalball.assetmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findByBaseId(Long baseId);

    List<Expenditure> findByAssetId(Long assetId);
}