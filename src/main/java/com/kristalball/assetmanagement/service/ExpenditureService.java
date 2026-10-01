package com.kristalball.assetmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.Expenditure;
import com.kristalball.assetmanagement.repository.ExpenditureRepository;

@Service
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;

    public ExpenditureService(ExpenditureRepository expenditureRepository) {
        this.expenditureRepository = expenditureRepository;
    }

    public List<Expenditure> getAllExpenditures() {
        return expenditureRepository.findAll();
    }

    public Expenditure getExpenditureById(Long id) {
        return expenditureRepository.findById(id).orElse(null);
    }

    public Expenditure saveExpenditure(Expenditure expenditure) {
        return expenditureRepository.save(expenditure);
    }

    public void deleteExpenditure(Long id) {
        expenditureRepository.deleteById(id);
    }

    public List<Expenditure> getExpendituresByBase(Long baseId) {
        return expenditureRepository.findByBaseId(baseId);
    }

    public List<Expenditure> getExpendituresByAsset(Long assetId) {
        return expenditureRepository.findByAssetId(assetId);
    }
}