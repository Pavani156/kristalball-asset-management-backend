package com.kristalball.assetmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.Transfer;
import com.kristalball.assetmanagement.service.AccessService;
import com.kristalball.assetmanagement.service.AuditLogService;
import com.kristalball.assetmanagement.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private final AuditLogService auditLogService;
    private final AccessService accessService;

    public TransferController(
            TransferService transferService,
            AuditLogService auditLogService,
            AccessService accessService) {

        this.transferService = transferService;
        this.auditLogService = auditLogService;
        this.accessService = accessService;
    }

    @GetMapping
    public List<Transfer> getAllTransfers(
            @RequestParam(required = false) Long fromBaseId,
            @RequestParam(required = false) Long toBaseId,
            @RequestParam(required = false) Long assetId,
            @RequestParam(required = false) String status) {

        if (accessService.isCommander()) {
            Long assignedBaseId = accessService.assignedBaseId();
            if (assignedBaseId == null) {
                return List.of();
            }

            return transferService.getAllTransfers().stream()
                    .filter(t -> t.getFromBase() != null
                            && t.getToBase() != null
                            && (assignedBaseId.equals(t.getFromBase().getId())
                            || assignedBaseId.equals(t.getToBase().getId())))
                    .filter(t -> fromBaseId == null
                            || fromBaseId.equals(t.getFromBase().getId()))
                    .filter(t -> toBaseId == null
                            || toBaseId.equals(t.getToBase().getId()))
                    .filter(t -> assetId == null
                            || (t.getAsset() != null
                            && assetId.equals(t.getAsset().getId())))
                    .filter(t -> status == null
                            || status.isBlank()
                            || status.equalsIgnoreCase(t.getStatus()))
                    .toList();
        }

        List<Transfer> transfers = transferService.getAllTransfers();

        return transfers.stream()
                .filter(t -> fromBaseId == null
                        || (t.getFromBase() != null
                        && fromBaseId.equals(t.getFromBase().getId())))
                .filter(t -> toBaseId == null
                        || (t.getToBase() != null
                        && toBaseId.equals(t.getToBase().getId())))
                .filter(t -> assetId == null
                        || (t.getAsset() != null
                        && assetId.equals(t.getAsset().getId())))
                .filter(t -> status == null
                        || status.isBlank()
                        || status.equalsIgnoreCase(t.getStatus()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transfer> getTransferById(
            @PathVariable Long id) {

        Transfer transfer = transferService.getTransferById(id);

        if (transfer == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireTransferBases(
                transfer.getFromBase().getId(),
                transfer.getToBase().getId());

        return ResponseEntity.ok(transfer);
    }

    @PostMapping
    public ResponseEntity<Transfer> createTransfer(
            @RequestBody Transfer transfer) {

        if (transfer.getFromBase() == null
                || transfer.getToBase() == null
                || transfer.getAsset() == null
                || transfer.getFromBase().getId() == null
                || transfer.getToBase().getId() == null
                || transfer.getAsset().getId() == null
                || transfer.getQuantity() == null
                || transfer.getQuantity() <= 0) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireTransferBases(
                transfer.getFromBase().getId(),
                transfer.getToBase().getId());

        if (transfer.getTransferDate() == null) {
            transfer.setTransferDate(
                    java.time.LocalDateTime.now());
        }

        if (transfer.getStatus() == null
                || transfer.getStatus().isBlank()) {
            transfer.setStatus("COMPLETED");
        }

        Transfer saved = transferService.saveTransfer(transfer);

        auditLogService.log(
                "CREATE",
                "TRANSFER",
                saved.getId(),
                currentUsername(),
                "Transfer "
                        + saved.getQuantity()
                        + " asset(s) from base "
                        + saved.getFromBase().getId()
                        + " to base "
                        + saved.getToBase().getId());

        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transfer> updateTransfer(
            @PathVariable Long id,
            @RequestBody Transfer transfer) {

        Transfer existing = transferService.getTransferById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (transfer.getFromBase() == null
                || transfer.getToBase() == null
                || transfer.getAsset() == null
                || transfer.getFromBase().getId() == null
                || transfer.getToBase().getId() == null
                || transfer.getAsset().getId() == null
                || transfer.getQuantity() == null
                || transfer.getQuantity() <= 0) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireTransferBases(
                existing.getFromBase().getId(),
                existing.getToBase().getId());

        accessService.requireTransferBases(
                transfer.getFromBase().getId(),
                transfer.getToBase().getId());

        existing.setTransferDate(transfer.getTransferDate());
        existing.setQuantity(transfer.getQuantity());
        existing.setStatus(transfer.getStatus());
        existing.setAsset(transfer.getAsset());
        existing.setFromBase(transfer.getFromBase());
        existing.setToBase(transfer.getToBase());

        Transfer updated = transferService.saveTransfer(existing);

        auditLogService.log(
                "UPDATE",
                "TRANSFER",
                id,
                currentUsername(),
                "Transfer updated");

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransfer(
            @PathVariable Long id) {

        Transfer existing = transferService.getTransferById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireTransferBases(
                existing.getFromBase().getId(),
                existing.getToBase().getId());

        transferService.deleteTransfer(id);

        auditLogService.log(
                "DELETE",
                "TRANSFER",
                id,
                currentUsername(),
                "Transfer deleted");

        return ResponseEntity.noContent().build();
    }

    private String currentUsername() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
