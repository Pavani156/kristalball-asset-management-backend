package com.kristalball.assetmanagement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.kristalball.assetmanagement.entity.Expenditure;
import com.kristalball.assetmanagement.entity.Asset;
import com.kristalball.assetmanagement.repository.AssetRepository;
import com.kristalball.assetmanagement.entity.User;
import com.kristalball.assetmanagement.service.AccessService;
import com.kristalball.assetmanagement.service.AuditLogService;
import com.kristalball.assetmanagement.service.ExpenditureService;
import com.kristalball.assetmanagement.service.UserService;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;
    private final AuditLogService auditLogService;
    private final AccessService accessService;
    private final UserService userService;
    private final AssetRepository assetRepository;

    public ExpenditureController(
            ExpenditureService expenditureService,
            AuditLogService auditLogService,
            AccessService accessService,
            UserService userService,
            AssetRepository assetRepository) {

        this.expenditureService = expenditureService;
        this.auditLogService = auditLogService;
        this.accessService = accessService;
        this.userService = userService;
        this.assetRepository = assetRepository;
    }

    @GetMapping
    public List<Expenditure> getAllExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetId) {

        if (accessService.isCommander() && baseId == null) {
            baseId = accessService.assignedBaseId();
        }

        accessService.requireBase(baseId);

        if (baseId != null) {
            return expenditureService.getExpendituresByBase(baseId);
        }

        if (assetId != null) {
            return expenditureService.getExpendituresByAsset(assetId);
        }

        return expenditureService.getAllExpenditures();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expenditure> getExpenditureById(
            @PathVariable Long id) {

        Expenditure expenditure =
                expenditureService.getExpenditureById(id);

        if (expenditure == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(
                expenditure.getBase().getId());

        return ResponseEntity.ok(expenditure);
    }

    @PostMapping
    public ResponseEntity<Expenditure> createExpenditure(
            @RequestBody Expenditure expenditure) {

        if (expenditure.getBase() == null
                || expenditure.getAsset() == null
                || expenditure.getQuantity() == null
                || expenditure.getQuantity() <= 0) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(
                expenditure.getBase().getId());

        Asset asset = assetRepository.findById(expenditure.getAsset().getId()).orElse(null);
        if (asset == null || asset.getBase() == null
                || !expenditure.getBase().getId().equals(asset.getBase().getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (expenditure.getCategory() == null) {
            expenditure.setCategory(asset.getCategory());
        }

        if (expenditure.getExpenditureDate() == null) {
            expenditure.setExpenditureDate(
                    LocalDate.now());
        }

        if (expenditure.getExpendedDate() == null) {
            expenditure.setExpendedDate(
                    LocalDateTime.now());
        }

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User currentUser =
                userService.getUserByUsername(username);

        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        expenditure.setCreatedBy(currentUser);

        Expenditure saved =
                expenditureService.saveExpenditure(expenditure);

        auditLogService.log(
                "CREATE",
                "EXPENDITURE",
                saved.getId(),
                username,
                "Expenditure quantity="
                        + saved.getQuantity());

        return ResponseEntity
                .status(201)
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expenditure> updateExpenditure(
            @PathVariable Long id,
            @RequestBody Expenditure expenditure) {

        Expenditure existing =
                expenditureService.getExpenditureById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (expenditure.getBase() == null
                || expenditure.getAsset() == null
                || expenditure.getQuantity() == null
                || expenditure.getQuantity() <= 0) {

            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(
                existing.getBase().getId());

        accessService.requireBase(
                expenditure.getBase().getId());

        Asset asset = assetRepository.findById(expenditure.getAsset().getId()).orElse(null);
        if (asset == null || asset.getBase() == null
                || !expenditure.getBase().getId().equals(asset.getBase().getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (expenditure.getCategory() == null) {
            expenditure.setCategory(asset.getCategory());
        }

        existing.setQuantity(
                expenditure.getQuantity());

        existing.setReason(
                expenditure.getReason());

        existing.setExpenditureDate(
                expenditure.getExpenditureDate());

        existing.setExpendedDate(
                expenditure.getExpendedDate());

        existing.setDescription(
                expenditure.getDescription());

        existing.setAsset(
                expenditure.getAsset());

        existing.setBase(
                expenditure.getBase());

        existing.setCategory(
                expenditure.getCategory());

        Expenditure updated =
                expenditureService.saveExpenditure(existing);

        auditLogService.log(
                "UPDATE",
                "EXPENDITURE",
                id,
                currentUsername(),
                "Expenditure updated");

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenditure(
            @PathVariable Long id) {

        Expenditure existing =
                expenditureService.getExpenditureById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(
                existing.getBase().getId());

        expenditureService.deleteExpenditure(id);

        auditLogService.log(
                "DELETE",
                "EXPENDITURE",
                id,
                currentUsername(),
                "Expenditure deleted");

        return ResponseEntity.noContent().build();
    }

    private String currentUsername() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}