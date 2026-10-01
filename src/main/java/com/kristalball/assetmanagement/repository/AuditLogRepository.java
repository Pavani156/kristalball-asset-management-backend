package com.kristalball.assetmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityType(String entityType);

    List<AuditLog> findByEntityId(Long entityId);

    List<AuditLog> findByUsername(String username);

    List<AuditLog> findByAction(String action);
}