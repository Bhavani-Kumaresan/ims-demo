package com.htc.incidentmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htc.incidentmanagement.model.SLA;
import com.htc.incidentmanagement.service.SLAService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/slas")
@Tag(name = "SLA Management", description = "Service Level Agreement configuration APIs")
public class SLAController {

    private final SLAService slaService;

    public SLAController(SLAService slaService) {
        this.slaService = slaService;
    }

    @Operation(summary = "Get all SLAs", description = "Returns list of all configured Service Level Agreements by priority")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All SLAs retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SLA.class)))
    })
    @GetMapping("/getAllSLA")
    public ResponseEntity<List<SLA>> getAll() {
        return ResponseEntity.ok(slaService.getAllSlas());
    }

    @Operation(summary = "Get SLA by ID", description = "Returns a specific SLA configuration by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SLA found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SLA.class))),
            @ApiResponse(responseCode = "404", description = "SLA not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SLA> getById(@PathVariable Long id) {
        return ResponseEntity.ok(slaService.getSlaById(id));
    }

    @Operation(summary = "Create new SLA", description = "Creates a new SLA configuration. Priority must be unique (HIGH, MEDIUM, LOW)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SLA created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SLA.class))),
            @ApiResponse(responseCode = "400", description = "Priority already exists or invalid input", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<SLA> create(@RequestBody SLA sla) {
        SLA created = slaService.createSla(sla);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update SLA", description = "Updates an existing SLA's priority and resolution time by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SLA updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SLA.class))),
            @ApiResponse(responseCode = "404", description = "SLA not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate priority", content = @Content)
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<SLA> update(@PathVariable Long id, @RequestBody SLA sla) {
        return ResponseEntity.ok(slaService.updateSla(id, sla));
    }

    @Operation(summary = "Delete SLA", description = "Deletes an SLA configuration by ID. Cannot delete if tickets reference it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "SLA deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "SLA not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot delete - referenced by tickets", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        slaService.deleteSla(id);
        return ResponseEntity.noContent().build();
    }
}