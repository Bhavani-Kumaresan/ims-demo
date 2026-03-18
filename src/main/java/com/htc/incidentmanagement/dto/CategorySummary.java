package com.htc.incidentmanagement.dto;

import com.htc.incidentmanagement.model.Category;

import io.swagger.v3.oas.annotations.media.Schema;

// Minimal Category summary
@Schema(description = "Summary category info")
public class CategorySummary {
    private Long categoryId;
    private String categoryName;

    public CategorySummary(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
    }

    public CategorySummary() {
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    

    // getters...
}