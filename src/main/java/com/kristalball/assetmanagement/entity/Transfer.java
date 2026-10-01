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
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime transferDate;

    private Integer quantity;

    private String status;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "from_base_id", nullable = false)
    private Base fromBase;

    @ManyToOne
    @JoinColumn(name = "to_base_id", nullable = false)
    private Base toBase;

    public Transfer() {
    }

    public Transfer(Long id, LocalDateTime transferDate, Integer quantity,
                    String status, Asset asset, Base fromBase, Base toBase) {
        this.id = id;
        this.transferDate = transferDate;
        this.quantity = quantity;
        this.status = status;
        this.asset = asset;
        this.fromBase = fromBase;
        this.toBase = toBase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public Base getFromBase() {
        return fromBase;
    }

    public void setFromBase(Base fromBase) {
        this.fromBase = fromBase;
    }

    public Base getToBase() {
        return toBase;
    }

    public void setToBase(Base toBase) {
        this.toBase = toBase;
    }
}