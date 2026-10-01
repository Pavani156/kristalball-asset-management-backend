package com.kristalball.assetmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.AuditLog;
import com.kristalball.assetmanagement.service.AuditLogService;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<AuditLog> getAllAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action) {

        if (entityType != null && !entityType.isBlank()) {
            return auditLogService.getLogsByEntityType(entityType);
        }

        if (entityId != null) {
            return auditLogService.getLogsByEntityId(entityId);
        }

        if (username != null && !username.isBlank()) {
            return auditLogService.getLogsByUsername(username);
        }

        if (action != null && !action.isBlank()) {
            return auditLogService.getLogsByAction(action);
        }

        return auditLogService.getAllAuditLogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(
            @PathVariable Long id) {

        AuditLog auditLog = auditLogService.getAuditLogById(id);

        if (auditLog == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(auditLog);
    }
}