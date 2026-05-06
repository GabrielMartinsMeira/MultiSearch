package com.multisearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaterialDTO extends BaseItemDTO {

    @JsonProperty("MaterialID")
    private String materialId;

    @JsonProperty("MaterialName")
    private String materialName;

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
}
