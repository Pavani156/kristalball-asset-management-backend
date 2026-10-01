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

import com.kristalball.assetmanagement.entity.Assignment;
import com.kristalball.assetmanagement.entity.Asset;
import com.kristalball.assetmanagement.repository.AssetRepository;
import com.kristalball.assetmanagement.service.AccessService;
import com.kristalball.assetmanagement.service.AssignmentService;
import com.kristalball.assetmanagement.service.AuditLogService;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AuditLogService auditLogService;
    private final AccessService accessService;
    private final AssetRepository assetRepository;

    public AssignmentController(
            AssignmentService assignmentService,
            AuditLogService auditLogService,
            AccessService accessService,
            AssetRepository assetRepository) {
        this.assignmentService = assignmentService;
        this.auditLogService = auditLogService;
        this.accessService = accessService;
        this.assetRepository = assetRepository;
    }

    @GetMapping
    public List<Assignment> getAllAssignments(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long assetId,
            @RequestParam(required = false) String status) {

        if (accessService.isCommander() && baseId == null) {
            baseId = accessService.assignedBaseId();
        }

        accessService.requireBase(baseId);

        if (baseId != null) {
            return assignmentService.getAssignmentsByBase(baseId);
        }

        if (assetId != null) {
            return assignmentService.getAssignmentsByAsset(assetId);
        }

        if (status != null && !status.isBlank()) {
            return assignmentService.getAssignmentsByStatus(status);
        }

        return assignmentService.getAllAssignments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(
            @PathVariable Long id) {

        Assignment assignment =
                assignmentService.getAssignmentById(id);

        if (assignment == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(assignment.getBase().getId());
        return ResponseEntity.ok(assignment);
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(
            @RequestBody Assignment assignment) {

        if (assignment.getBase() == null
                || assignment.getAsset() == null
                || assignment.getQuantity() == null
                || assignment.getQuantity() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(assignment.getBase().getId());

        Asset asset = assetRepository.findById(assignment.getAsset().getId()).orElse(null);
        if (asset == null || asset.getBase() == null
                || !assignment.getBase().getId().equals(asset.getBase().getId())) {
            return ResponseEntity.badRequest().build();
        }

        if (assignment.getAssignedDate() == null) {
            assignment.setAssignedDate(
                    java.time.LocalDateTime.now());
        }

        if (assignment.getStatus() == null
                || assignment.getStatus().isBlank()) {
            assignment.setStatus("ACTIVE");
        }

        Assignment saved =
                assignmentService.saveAssignment(assignment);

        auditLogService.log(
                "CREATE",
                "ASSIGNMENT",
                saved.getId(),
                currentUsername(),
                "Assignment to "
                        + saved.getPersonnelName()
                        + ", quantity="
                        + saved.getQuantity());

        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable Long id,
            @RequestBody Assignment assignment) {

        Assignment existing =
                assignmentService.getAssignmentById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        if (assignment.getBase() == null
                || assignment.getAsset() == null
                || assignment.getQuantity() == null
                || assignment.getQuantity() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        accessService.requireBase(existing.getBase().getId());
        accessService.requireBase(assignment.getBase().getId());

        Asset asset = assetRepository.findById(assignment.getAsset().getId()).orElse(null);
        if (asset == null || asset.getBase() == null
                || !assignment.getBase().getId().equals(asset.getBase().getId())) {
            return ResponseEntity.badRequest().build();
        }

        existing.setPersonnelName(
                assignment.getPersonnelName());
        existing.setQuantity(assignment.getQuantity());
        existing.setAssignedDate(assignment.getAssignedDate());
        existing.setStatus(assignment.getStatus());
        existing.setAsset(assignment.getAsset());
        existing.setBase(assignment.getBase());

        Assignment updated =
                assignmentService.saveAssignment(existing);

        auditLogService.log(
                "UPDATE",
                "ASSIGNMENT",
                id,
                currentUsername(),
                "Assignment updated");

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long id) {

        Assignment existing =
                assignmentService.getAssignmentById(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        accessService.requireBase(existing.getBase().getId());

        assignmentService.deleteAssignment(id);

        auditLogService.log(
                "DELETE",
                "ASSIGNMENT",
                id,
                currentUsername(),
                "Assignment deleted");

        return ResponseEntity.noContent().build();
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
