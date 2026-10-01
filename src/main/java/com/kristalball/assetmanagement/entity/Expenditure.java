package com.kristalball.assetmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenditures")
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String reason;

    @Column(name = "expenditure_date", nullable = false)
    private LocalDate expenditureDate;

    @Column(name = "expended_date")
    private LocalDateTime expendedDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategory category;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    public Expenditure() {
    }

    public Expenditure(
            Long id,
            Integer quantity,
            String reason,
            LocalDate expenditureDate,
            LocalDateTime expendedDate,
            String description,
            Asset asset,
            Base base,
            AssetCategory category,
            User createdBy) {

        this.id = id;
        this.quantity = quantity;
        this.reason = reason;
        this.expenditureDate = expenditureDate;
        this.expendedDate = expendedDate;
        this.description = description;
        this.asset = asset;
        this.base = base;
        this.category = category;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getExpenditureDate() {
        return expenditureDate;
    }

    public void setExpenditureDate(LocalDate expenditureDate) {
        this.expenditureDate = expenditureDate;
    }

    public LocalDateTime getExpendedDate() {
        return expendedDate;
    }

    public void setExpendedDate(LocalDateTime expendedDate) {
        this.expendedDate = expendedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}