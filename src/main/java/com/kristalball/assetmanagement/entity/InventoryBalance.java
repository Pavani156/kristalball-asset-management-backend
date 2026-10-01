package com.kristalball.assetmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "inventory_balances",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "base_id",
                        "category_id"
                }))
public class InventoryBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer openingBalance = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "base_id")
    private Base base;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private AssetCategory category;

    public InventoryBalance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(
            Integer openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(
            AssetCategory category) {
        this.category = category;
    }
}
