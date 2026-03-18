package com.htc.incidentmanagement.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ims_category")
@Schema(description = "Ticket category for classification")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<IncidentTicket> tickets;

    public Category() {
    }

    public Category(Long categoryId, String categoryName, List<IncidentTicket> tickets) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.tickets = tickets;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<IncidentTicket> getTickets() {
        return tickets;
    }

    public void setTickets(List<IncidentTicket> tickets) {
        this.tickets = tickets;
    }

}
