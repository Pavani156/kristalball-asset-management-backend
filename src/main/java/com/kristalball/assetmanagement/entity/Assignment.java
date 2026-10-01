package com.kristalball.assetmanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personnelName;

    private Integer quantity;

    private LocalDateTime assignedDate;

    private String status;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    public Assignment() {
    }

    public Assignment(Long id, String personnelName, Integer quantity,
                       LocalDateTime assignedDate, String status,
                       Asset asset, Base base) {
        this.id = id;
        this.personnelName = personnelName;
        this.quantity = quantity;
        this.assignedDate = assignedDate;
        this.status = status;
        this.asset = asset;
        this.base = base;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }
}