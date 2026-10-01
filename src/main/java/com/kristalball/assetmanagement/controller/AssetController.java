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

import com.kristalball.assetmanagement.entity.Asset;
import com.kristalball.assetmanagement.service.AccessService;
import com.kristalball.assetmanagement.service.AssetService;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;
    private final AccessService accessService;

    public AssetController(
            AssetService assetService,
            AccessService accessService) {
        this.assetService = assetService;
        this.accessService = accessService;
    }

    @GetMapping
    public List<Asset> getAllAssets(
            @RequestParam(required = false) Long baseId) {

        if (accessService.isCommander()) {
            baseId = accessService.assignedBaseId();
        }

        if (baseId != null) {
            accessService.requireBase(baseId);

            final Long selectedBase = baseId;

            return assetService.getAllAssets()
                    .stream()
                    .filter(asset ->
                            asset.getBase() != null
                                    && selectedBase.equals(
                                            asset.getBase().getId()))
                    .toList();
        }

        return assetService.getAllAssets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(
            @PathVariable Long id) {

        Asset asset = assetService.getAssetById(id);

        if (asset == null) {
            return ResponseEntity.notFound().build();
        }

        if (asset.getBase() != null) {
            accessService.requireBase(
                    asset.getBase().getId());
        }

        return ResponseEntity.ok(asset);
    }

    @PostMapping
    public ResponseEntity<Asset> createAsset(
            @RequestBody Asset asset) {

        if (!accessService.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can modify assets");
        }

        Asset savedAsset =
                assetService.saveAsset(asset);

        return ResponseEntity.status(201)
                .body(savedAsset);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable Long id,
            @RequestBody Asset asset) {

        if (!accessService.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can modify assets");
        }

        Asset existing =
                assetService.getAssetById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(asset.getName());
        existing.setDescription(asset.getDescription());
        existing.setStatus(asset.getStatus());
        existing.setSerialNumber(
                asset.getSerialNumber());
        existing.setCategory(asset.getCategory());
        existing.setBase(asset.getBase());

        return ResponseEntity.ok(
                assetService.saveAsset(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable Long id) {

        if (!accessService.isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Only administrators can modify assets");
        }

        if (assetService.getAssetById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        assetService.deleteAsset(id);

        return ResponseEntity.noContent().build();
    }
}
