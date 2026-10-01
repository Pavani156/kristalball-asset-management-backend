package com.kristalball.assetmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.Base;
import com.kristalball.assetmanagement.service.BaseService;

@RestController
@RequestMapping("/api/bases")
public class BaseController {

    private final BaseService baseService;

    public BaseController(BaseService baseService) {

        this.baseService = baseService;

    }

    // GET all bases
    @GetMapping
    public List<Base> getAllBases() {

        return baseService.getAllBases();

    }

    // GET base by ID
    @GetMapping("/{id}")
    public ResponseEntity<Base> getBaseById(@PathVariable Long id) {

        Base base = baseService.getBaseById(id);

        if (base == null) {

            return ResponseEntity.notFound().build();

        }

        return ResponseEntity.ok(base);

    }

    // CREATE base
    @PostMapping
    public ResponseEntity<Base> createBase(@RequestBody Base base) {

        return ResponseEntity.ok(
                baseService.saveBase(base)
        );

    }

    // UPDATE base
    @PutMapping("/{id}")
    public ResponseEntity<Base> updateBase(
            @PathVariable Long id,
            @RequestBody Base base) {

        Base updatedBase = baseService.updateBase(id, base);

        if (updatedBase == null) {

            return ResponseEntity.notFound().build();

        }

        return ResponseEntity.ok(updatedBase);

    }

    // DELETE base
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBase(@PathVariable Long id) {

        Base existingBase = baseService.getBaseById(id);

        if (existingBase == null) {

            return ResponseEntity.notFound().build();

        }

        baseService.deleteBase(id);

        return ResponseEntity.noContent().build();

    }

}