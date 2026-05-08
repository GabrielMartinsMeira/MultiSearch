package com.multisearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SalesOrderDTO extends BaseItemDTO {

    @JsonProperty("MaterialID")
    private String materialId;

    @JsonProperty("MaterialName")
    private String materialName;

    @JsonProperty("SalesOrderID")
    private int salesOrderId;

    @JsonProperty("Customer")
    private String customer;

    @JsonProperty("DeliveryDate")
    private String deliveryDate;

    @JsonProperty("Quantity")
    private int quantity;

    @JsonProperty("TotalValue")
    private double totalValue;

    public int getSalesOrderId() {
        return salesOrderId;
    }

    public void setSalesOrderId(int salesOrderId) {
        this.salesOrderId = salesOrderId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
