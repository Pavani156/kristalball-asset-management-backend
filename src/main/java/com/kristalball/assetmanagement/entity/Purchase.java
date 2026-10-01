package com.kristalball.assetmanagement.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate purchaseDate;

    private Integer quantity;

    private String supplier;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategory category;

    @ManyToOne
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    public Purchase() {
    }

    public Purchase(
            Long id,
            LocalDate purchaseDate,
            Integer quantity,
            String supplier,
            AssetCategory category,
            Base base) {
        this.id = id;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.supplier = supplier;
        this.category = category;
        this.base = base;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
