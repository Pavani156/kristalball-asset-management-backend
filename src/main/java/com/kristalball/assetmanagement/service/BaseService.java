package com.kristalball.assetmanagement.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kristalball.assetmanagement.entity.Base;
import com.kristalball.assetmanagement.repository.BaseRepository;

@Service
public class BaseService {

    private final BaseRepository baseRepository;

    public BaseService(BaseRepository baseRepository) {

        this.baseRepository = baseRepository;

    }

    public List<Base> getAllBases() {

        return baseRepository.findAll();

    }

    public Base getBaseById(Long id) {

        return baseRepository.findById(id).orElse(null);

    }

    public Base saveBase(Base base) {

        return baseRepository.save(base);

    }

    public Base updateBase(Long id, Base base) {

        Base existingBase = baseRepository.findById(id).orElse(null);

        if (existingBase == null) {
            return null;
        }

        existingBase.setName(base.getName());
        existingBase.setLocation(base.getLocation());

        return baseRepository.save(existingBase);

    }

    public void deleteBase(Long id) {

        baseRepository.deleteById(id);

    }

}