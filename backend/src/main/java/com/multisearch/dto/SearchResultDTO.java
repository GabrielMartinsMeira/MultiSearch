package com.multisearch.dto;

import java.util.List;

public class SearchResultDTO {
    private String category;
    private List<?> items;

    public SearchResultDTO() {
    }

    public SearchResultDTO(String category, List<?> items) {
        this.category = category;
        this.items = items;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<?> getItems() {
        return items;
    }

    public void setItems(List<?> items) {
        this.items = items;
    }
}
