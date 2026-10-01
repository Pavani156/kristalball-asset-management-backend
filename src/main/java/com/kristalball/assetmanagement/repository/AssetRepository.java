package com.kristalball.assetmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {

}