package com.kristalball.assetmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kristalball.assetmanagement.entity.Asset;
import com.kristalball.assetmanagement.entity.Base;
import com.kristalball.assetmanagement.entity.Transfer;
import com.kristalball.assetmanagement.repository.AssetRepository;
import com.kristalball.assetmanagement.repository.BaseRepository;
import com.kristalball.assetmanagement.repository.TransferRepository;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final BaseRepository baseRepository;
    private final AssetRepository assetRepository;

    public TransferService(TransferRepository transferRepository,
                           BaseRepository baseRepository,
                           AssetRepository assetRepository) {
        this.transferRepository = transferRepository;
        this.baseRepository = baseRepository;
        this.assetRepository = assetRepository;
    }

    public List<Transfer> getAllTransfers() { return transferRepository.findAll(); }

    public Transfer getTransferById(Long id) {
        return transferRepository.findById(id).orElse(null);
    }

    @Transactional
    public Transfer saveTransfer(Transfer transfer) {
        validateRequest(transfer);

        Base fromBase = baseRepository.findById(transfer.getFromBase().getId())
                .orElseThrow(() -> new IllegalArgumentException("From base not found: " + transfer.getFromBase().getId()));
        Base toBase = baseRepository.findById(transfer.getToBase().getId())
                .orElseThrow(() -> new IllegalArgumentException("To base not found: " + transfer.getToBase().getId()));
        Asset asset = assetRepository.findById(transfer.getAsset().getId())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + transfer.getAsset().getId()));

        if (fromBase.getId().equals(toBase.getId())) {
            throw new IllegalArgumentException("Source and destination bases must be different");
        }

        // When editing an already completed transfer, first restore its previous asset location.
        if (transfer.getId() != null) {
            Transfer previous = transferRepository.findById(transfer.getId()).orElse(null);
            if (previous != null && isCompleted(previous) && previous.getAsset() != null) {
                Asset previousAsset = assetRepository.findById(previous.getAsset().getId()).orElse(null);
                if (previousAsset != null) {
                    Base previousFrom = baseRepository.findById(previous.getFromBase().getId()).orElse(null);
                    if (previousFrom != null) previousAsset.setBase(previousFrom);
                    assetRepository.save(previousAsset);
                    if (previousAsset.getId().equals(asset.getId())) {
                        asset = previousAsset;
                    }
                }
            }
        }

        transfer.setFromBase(fromBase);
        transfer.setToBase(toBase);
        transfer.setAsset(asset);

        if (isCompleted(transfer)) {
            if (asset.getBase() == null || !fromBase.getId().equals(asset.getBase().getId())) {
                throw new IllegalArgumentException("Asset is not currently located at the source base");
            }
            asset.setBase(toBase);
            assetRepository.save(asset);
        }

        return transferRepository.save(transfer);
    }

    @Transactional
    public void deleteTransfer(Long id) {
        Transfer existing = transferRepository.findById(id).orElse(null);
        if (existing == null) return;
        if (isCompleted(existing) && existing.getAsset() != null) {
            Asset asset = assetRepository.findById(existing.getAsset().getId()).orElse(null);
            if (asset != null && existing.getFromBase() != null) {
                asset.setBase(existing.getFromBase());
                assetRepository.save(asset);
            }
        }
        transferRepository.deleteById(id);
    }

    public List<Transfer> getTransfersByFromBase(Long fromBaseId) { return transferRepository.findByFromBaseId(fromBaseId); }
    public List<Transfer> getTransfersByToBase(Long toBaseId) { return transferRepository.findByToBaseId(toBaseId); }
    public List<Transfer> getTransfersByAsset(Long assetId) { return transferRepository.findByAssetId(assetId); }
    public List<Transfer> getTransfersByStatus(String status) { return transferRepository.findByStatus(status); }

    private void validateRequest(Transfer transfer) {
        if (transfer == null || transfer.getFromBase() == null || transfer.getFromBase().getId() == null
                || transfer.getToBase() == null || transfer.getToBase().getId() == null
                || transfer.getAsset() == null || transfer.getAsset().getId() == null
                || transfer.getQuantity() == null || transfer.getQuantity() <= 0) {
            throw new IllegalArgumentException("From base, destination base, asset and positive quantity are required");
        }
    }

    private boolean isCompleted(Transfer transfer) {
        return transfer.getStatus() == null || "COMPLETED".equalsIgnoreCase(transfer.getStatus());
    }
}
