package com.kristalball.assetmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String status;

    private String serialNumber;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private AssetCategory category;

    @ManyToOne
    @JoinColumn(name = "base_id")
    private Base base;

    public Asset() {
    }

    public Asset(Long id, String name, String description, String status,
                 String serialNumber, AssetCategory category, Base base) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.serialNumber = serialNumber;
        this.category = category;
        this.base = base;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }
}