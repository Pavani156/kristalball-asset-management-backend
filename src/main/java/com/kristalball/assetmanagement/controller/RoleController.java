package com.kristalball.assetmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kristalball.assetmanagement.entity.Role;
import com.kristalball.assetmanagement.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {

        Role role = roleService.getRoleById(id);

        if (role == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.saveRole(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {

        roleService.deleteRole(id);

        return ResponseEntity.noContent().build();
    }
}