package com.kristalball.assetmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.AssetCategory;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {

}