package com.kristalball.assetmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByBaseId(Long baseId);

    List<Assignment> findByAssetId(Long assetId);

    List<Assignment> findByStatus(String status);
}