package com.multisearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EquipmentDTO extends BaseItemDTO {

    @JsonProperty("EquipmentID")
    private String equipmentId;

    @JsonProperty("EquipmentName")
    private String equipmentName;

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }
}
