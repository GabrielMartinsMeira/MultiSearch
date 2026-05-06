package com.multisearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkforceDTO extends BaseItemDTO {

    @JsonProperty("WorkforceID")
    private int workforceId;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Shift")
    private String shift;

    public int getWorkforceId() {
        return workforceId;
    }

    public void setWorkforceId(int workforceId) {
        this.workforceId = workforceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}
