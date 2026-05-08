package com.multisearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PurchaseOrderDTO extends BaseItemDTO {

    @JsonProperty("MaterialID")
    private String materialId;

    @JsonProperty("MaterialName")
    private String materialName;

    @JsonProperty("PurchaseOrderID")
    private int purchaseOrderId;

    @JsonProperty("Supplier")
    private String supplier;

    @JsonProperty("DeliveryDate")
    private String deliveryDate;

    @JsonProperty("Quantity")
    private int quantity;

    @JsonProperty("TotalCost")
    private double totalCost;

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
