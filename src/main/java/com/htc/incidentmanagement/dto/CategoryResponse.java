package com.htc.incidentmanagement.dto;


import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category response without circular references")
public class CategoryResponse {

    @Schema(example = "1")
    private Long categoryId;

    @Schema(example = "Network")
    private String categoryName;

    private List<TicketSummary> tickets;

    public CategoryResponse() {
    }

    public CategoryResponse(Long categoryId, String categoryName, List<TicketSummary> tickets) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.tickets = tickets;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<TicketSummary> getTickets() {
        return tickets;
    }
}
