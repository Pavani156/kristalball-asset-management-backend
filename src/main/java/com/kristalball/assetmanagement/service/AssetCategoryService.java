package com.kristalball.assetmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.AssetCategory;
import com.kristalball.assetmanagement.repository.AssetCategoryRepository;

@Service
public class AssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;

    public AssetCategoryService(AssetCategoryRepository assetCategoryRepository) {
        this.assetCategoryRepository = assetCategoryRepository;
    }

    public List<AssetCategory> getAllCategories() {
        return assetCategoryRepository.findAll();
    }

    public AssetCategory getCategoryById(Long id) {
        return assetCategoryRepository.findById(id).orElse(null);
    }

    public AssetCategory saveCategory(AssetCategory category) {
        return assetCategoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        assetCategoryRepository.deleteById(id);
    }
}