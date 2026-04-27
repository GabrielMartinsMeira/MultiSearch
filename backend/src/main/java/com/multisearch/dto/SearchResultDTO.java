package com.multisearch.dto;

import java.util.List;
import java.util.Map;

public class SearchResultDTO {
    private String category;
    private List<Map<String, Object>> items;

    public SearchResultDTO() {}

    public SearchResultDTO(String category, List<Map<String, Object>> items) {
        this.category = category;
        this.items = items;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }
}
