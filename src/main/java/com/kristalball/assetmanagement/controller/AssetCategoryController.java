package com.kristalball.assetmanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.AssetCategory;
import com.kristalball.assetmanagement.repository.AssetCategoryRepository;

@RestController
@RequestMapping("/api/categories")
public class AssetCategoryController {

    private final AssetCategoryRepository assetCategoryRepository;

    public AssetCategoryController(
            AssetCategoryRepository assetCategoryRepository) {
        this.assetCategoryRepository = assetCategoryRepository;
    }

    // GET all categories
    @GetMapping
    public List<AssetCategory> getAllCategories() {
        return assetCategoryRepository.findAll();
    }

    // GET category by ID
    @GetMapping("/{id}")
    public ResponseEntity<AssetCategory> getCategoryById(
            @PathVariable Long id) {

        Optional<AssetCategory> category =
                assetCategoryRepository.findById(id);

        if (category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category.get());
    }

    // CREATE category
    @PostMapping
    public ResponseEntity<AssetCategory> createCategory(
            @RequestBody AssetCategory category) {

        AssetCategory savedCategory =
                assetCategoryRepository.save(category);

        return ResponseEntity.ok(savedCategory);
    }

    // UPDATE category
    @PutMapping("/{id}")
    public ResponseEntity<AssetCategory> updateCategory(
            @PathVariable Long id,
            @RequestBody AssetCategory category) {

        Optional<AssetCategory> existing =
                assetCategoryRepository.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AssetCategory existingCategory = existing.get();

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        AssetCategory updatedCategory =
                assetCategoryRepository.save(existingCategory);

        return ResponseEntity.ok(updatedCategory);
    }

    // DELETE category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        if (!assetCategoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        assetCategoryRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}