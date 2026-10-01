package com.kristalball.assetmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}