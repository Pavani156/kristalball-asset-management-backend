package com.kristalball.assetmanagement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.Asset;
import com.kristalball.assetmanagement.entity.AssetCategory;
import com.kristalball.assetmanagement.entity.Assignment;
import com.kristalball.assetmanagement.entity.Base;
import com.kristalball.assetmanagement.entity.Expenditure;
import com.kristalball.assetmanagement.entity.InventoryBalance;
import com.kristalball.assetmanagement.entity.Purchase;
import com.kristalball.assetmanagement.entity.Transfer;
import com.kristalball.assetmanagement.repository.AssignmentRepository;
import com.kristalball.assetmanagement.repository.ExpenditureRepository;
import com.kristalball.assetmanagement.repository.InventoryBalanceRepository;
import com.kristalball.assetmanagement.repository.PurchaseRepository;
import com.kristalball.assetmanagement.repository.TransferRepository;
import com.kristalball.assetmanagement.service.AccessService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final InventoryBalanceRepository inventoryRepository;
    private final AccessService accessService;

    public DashboardController(
            PurchaseRepository purchaseRepository,
            TransferRepository transferRepository,
            AssignmentRepository assignmentRepository,
            ExpenditureRepository expenditureRepository,
            InventoryBalanceRepository inventoryRepository,
            AccessService accessService) {
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.inventoryRepository = inventoryRepository;
        this.accessService = accessService;
    }

    @GetMapping
    public Map<String, Object> getDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long categoryId) {

        if (accessService.isCommander() && baseId == null) {
            baseId = accessService.assignedBaseId();
        }
        accessService.requireBase(baseId);

        LocalDate start = startDate == null ? LocalDate.of(2000, 1, 1) : startDate;
        LocalDate end = endDate == null ? LocalDate.now() : endDate;
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endDate must be on or after startDate");
        }

        final Long selectedBase = baseId;
        final Long selectedCategory = categoryId;
        final LocalDateTime periodStart = start.atStartOfDay();
        final LocalDateTime periodEndExclusive = end.plusDays(1).atStartOfDay();

        int opening = inventoryRepository.findAll().stream()
                .filter(i -> matchesBase(i.getBase(), selectedBase))
                .filter(i -> matchesCategory(i.getCategory(), selectedCategory))
                .mapToInt(i -> safe(i.getOpeningBalance()))
                .sum();

        int purchases = 0;
        int transferIn = 0;
        int transferOut = 0;
        int assigned = 0;
        int expended = 0;

        for (Purchase p : purchaseRepository.findAll()) {
            if (!matchesBase(p.getBase(), selectedBase)
                    || !matchesCategory(p.getCategory(), selectedCategory)
                    || p.getPurchaseDate() == null) {
                continue;
            }
            if (p.getPurchaseDate().isBefore(start)) {
                opening += safe(p.getQuantity());
            } else if (!p.getPurchaseDate().isAfter(end)) {
                purchases += safe(p.getQuantity());
            }
        }

        for (Transfer t : transferRepository.findAll()) {
            if (t.getStatus() != null && !"COMPLETED".equalsIgnoreCase(t.getStatus())) {
                continue;
            }
            if (!matchesAssetCategory(t.getAsset(), selectedCategory)
                    || t.getTransferDate() == null) {
                continue;
            }
            boolean fromSelected = matchesBase(t.getFromBase(), selectedBase);
            boolean toSelected = matchesBase(t.getToBase(), selectedBase);
            if (selectedBase == null) {
                fromSelected = t.getFromBase() != null;
                toSelected = t.getToBase() != null;
            }

            if (t.getTransferDate().isBefore(periodStart)) {
                if (toSelected) {
                    opening += safe(t.getQuantity());
                }
                if (fromSelected) {
                    opening -= safe(t.getQuantity());
                }
            } else if (t.getTransferDate().isBefore(periodEndExclusive)) {
                if (toSelected) {
                    transferIn += safe(t.getQuantity());
                }
                if (fromSelected) {
                    transferOut += safe(t.getQuantity());
                }
            }
        }

        for (Assignment a : assignmentRepository.findAll()) {
            if (!matchesBase(a.getBase(), selectedBase)
                    || !matchesAssetCategory(a.getAsset(), selectedCategory)
                    || a.getAssignedDate() == null) {
                continue;
            }
            if (a.getAssignedDate().isBefore(periodStart)) {
                opening -= safe(a.getQuantity());
            } else if (a.getAssignedDate().isBefore(periodEndExclusive)) {
                assigned += safe(a.getQuantity());
            }
        }

        for (Expenditure e : expenditureRepository.findAll()) {
            if (!matchesBase(e.getBase(), selectedBase)
                    || !matchesAssetCategory(e.getAsset(), selectedCategory)
                    || e.getExpendedDate() == null) {
                continue;
            }
            if (e.getExpendedDate().isBefore(periodStart)) {
                opening -= safe(e.getQuantity());
            } else if (e.getExpendedDate().isBefore(periodEndExclusive)) {
                expended += safe(e.getQuantity());
            }
        }

        int netMovement = purchases + transferIn - transferOut;
        int closing = opening + netMovement - assigned - expended;

        List<Purchase> purchaseDetails = purchaseRepository.findAll().stream()
                .filter(p -> p.getPurchaseDate() != null
                        && !p.getPurchaseDate().isBefore(start)
                        && !p.getPurchaseDate().isAfter(end))
                .filter(p -> matchesBase(p.getBase(), selectedBase))
                .filter(p -> matchesCategory(p.getCategory(), selectedCategory))
                .toList();

        List<Transfer> transferInDetails = transferRepository.findAll().stream()
                .filter(t -> (t.getStatus() == null || "COMPLETED".equalsIgnoreCase(t.getStatus()))
                        && t.getTransferDate() != null
                        && !t.getTransferDate().isBefore(periodStart)
                        && t.getTransferDate().isBefore(periodEndExclusive))
                .filter(t -> matchesAssetCategory(t.getAsset(), selectedCategory))
                .filter(t -> t.getToBase() != null
                        && (selectedBase == null || selectedBase.equals(t.getToBase().getId())))
                .toList();

        List<Transfer> transferOutDetails = transferRepository.findAll().stream()
                .filter(t -> (t.getStatus() == null || "COMPLETED".equalsIgnoreCase(t.getStatus()))
                        && t.getTransferDate() != null
                        && !t.getTransferDate().isBefore(periodStart)
                        && t.getTransferDate().isBefore(periodEndExclusive))
                .filter(t -> matchesAssetCategory(t.getAsset(), selectedCategory))
                .filter(t -> t.getFromBase() != null
                        && (selectedBase == null || selectedBase.equals(t.getFromBase().getId())))
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startDate", start);
        result.put("endDate", end);
        result.put("baseId", selectedBase);
        result.put("categoryId", selectedCategory);
        result.put("openingBalance", opening);
        result.put("purchases", purchases);
        result.put("transferIn", transferIn);
        result.put("transferOut", transferOut);
        result.put("netMovement", netMovement);
        result.put("assigned", assigned);
        result.put("expended", expended);
        result.put("closingBalance", closing);

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("purchases", purchaseDetails);
        details.put("transferIn", transferInDetails);
        details.put("transferOut", transferOutDetails);
        result.put("netMovementDetails", details);
        return result;
    }

    private static int safe(Integer value) {
        return value == null ? 0 : value;
    }

    private static boolean matchesBase(Base base, Long baseId) {
        return baseId == null || (base != null && baseId.equals(base.getId()));
    }

    private static boolean matchesCategory(AssetCategory category, Long categoryId) {
        return categoryId == null || (category != null && categoryId.equals(category.getId()));
    }

    private static boolean matchesAssetCategory(Asset asset, Long categoryId) {
        return categoryId == null
                || (asset != null && matchesCategory(asset.getCategory(), categoryId));
    }
}
