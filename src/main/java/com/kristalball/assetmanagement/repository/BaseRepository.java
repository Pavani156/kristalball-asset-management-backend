package com.kristalball.assetmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kristalball.assetmanagement.entity.Base;

public interface BaseRepository extends JpaRepository<Base, Long> {

}