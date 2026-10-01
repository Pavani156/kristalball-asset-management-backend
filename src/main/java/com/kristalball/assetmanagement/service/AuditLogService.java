package com.kristalball.assetmanagement.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.AuditLog;
import com.kristalball.assetmanagement.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog log(
            String action,
            String entityType,
            Long entityId,
            String username,
            String details) {

        AuditLog auditLog = new AuditLog();

        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setUsername(username);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);

        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    public AuditLog getAuditLogById(Long id) {
        return auditLogRepository.findById(id)
                .orElse(null);
    }

    public AuditLog saveAuditLog(AuditLog auditLog) {

        if (auditLog.getTimestamp() == null) {
            auditLog.setTimestamp(
                    LocalDateTime.now());
        }

        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getLogsByEntityType(
            String entityType) {
        return auditLogRepository
                .findByEntityType(entityType);
    }

    public List<AuditLog> getLogsByEntityId(Long entityId) {
        return auditLogRepository
                .findByEntityId(entityId);
    }

    public List<AuditLog> getLogsByUsername(
            String username) {
        return auditLogRepository
                .findByUsername(username);
    }

    public List<AuditLog> getLogsByAction(
            String action) {
        return auditLogRepository.findByAction(action);
    }
}
