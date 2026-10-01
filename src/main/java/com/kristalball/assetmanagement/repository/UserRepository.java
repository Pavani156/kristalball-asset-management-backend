package com.kristalball.assetmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}