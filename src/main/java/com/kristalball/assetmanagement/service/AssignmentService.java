package com.kristalball.assetmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.Assignment;
import com.kristalball.assetmanagement.repository.AssignmentRepository;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElse(null);
    }

    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    public List<Assignment> getAssignmentsByBase(Long baseId) {
        return assignmentRepository.findByBaseId(baseId);
    }

    public List<Assignment> getAssignmentsByAsset(Long assetId) {
        return assignmentRepository.findByAssetId(assetId);
    }

    public List<Assignment> getAssignmentsByStatus(String status) {
        return assignmentRepository.findByStatus(status);
    }
}