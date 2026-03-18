package com.htc.incidentmanagement.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.htc.incidentmanagement.dto.CreateTicketRequest;
import com.htc.incidentmanagement.dto.TicketResponse;
import com.htc.incidentmanagement.model.IncidentTicket;
import com.htc.incidentmanagement.security.EmployeeUserDetails;
import com.htc.incidentmanagement.security.SecurityUtil;
import com.htc.incidentmanagement.service.IncidentTicketService;
import com.htc.incidentmanagement.service.TicketAssignmentService;
import com.htc.incidentmanagement.util.TicketMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Incident Tickets", description = "Incident ticket management")
public class IncidentTicketController {

        private static final Logger logger = LoggerFactory.getLogger(IncidentTicketController.class);

        private final IncidentTicketService ticketService;
        private final TicketAssignmentService ticketAssignmentService;

        @Autowired
        private TicketMapper ticketMapper;

        public IncidentTicketController(IncidentTicketService ticketService,
                        TicketAssignmentService ticketAssignmentService) {
                this.ticketService = ticketService;
                this.ticketAssignmentService = ticketAssignmentService;

        }

        @Operation(summary = "create a ticket", description = "Creates a new incident ticket. The ticket is initially unapproved and unassigned.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Ticket Created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
        })
        @PostMapping("/createTicket")
        public ResponseEntity<TicketResponse> createTicket(
                        @RequestBody @Valid CreateTicketRequest request,
                        @RequestParam Long categoryId,
                        @RequestParam Long slaId) {

                EmployeeUserDetails employeeUserDetails = SecurityUtil.getCurrentUser();
                logger.info("POST /tickets/createTicket - Title: {}, Creator: {}", request.getTitle(),
                                employeeUserDetails.getEmploeeId());

                TicketResponse ticket = ticketService.createTicket(request, employeeUserDetails.getEmploeeId(),
                                categoryId,
                                slaId);
                return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
        }

        @Operation(summary = "Assign a ticket", description = "Assigns an approved ticket to an agent. Tickets must be approved before assignment.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket will be Assigned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
        })
        @PutMapping("/{ticketId}/assign")
        @Transactional
        public ResponseEntity<TicketResponse> assignTicket(
                        @PathVariable Long ticketId,
                        @RequestParam Long employeeId,
                        @RequestParam(required = false) String comment) {

                IncidentTicket updatedTicket = ticketService.assignTicket(ticketId, employeeId, comment);
                logger.error("AFTER ASSIGN - createdBy={}, assignedTo={}",
                                updatedTicket.getCreatedBy().getEmployeeId(),
                                updatedTicket.getAssignedTo().getEmployeeId());
                return ResponseEntity.ok(ticketMapper.mapToResponse(updatedTicket));
        }

        @Operation(summary = "Get all tickets", description = "Retrieves all tickets based on access level. Admins can view all tickets; agents and users see only relevant tickets.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket list will be returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
        })
        @GetMapping("/getAllTickets")

        public ResponseEntity<List<TicketResponse>> getAllTickets() {
                List<IncidentTicket> tickets = ticketService.getAllTickets();
                List<TicketResponse> responses = tickets.stream()
                                .map(ticketMapper::mapToResponse)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(responses);
        }

        @Operation(summary = "Get ticket by TicketID", description = "Retrieves complete ticket information including assignment, SLA, and status.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Specific Ticket will be returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
        })
        @GetMapping("/{ticketId}")
        public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long ticketId) {
                IncidentTicket ticket = ticketService.getTicketById(ticketId);
                logger.error("AFTER FETCH - createdBy={}, assignedTo={}",
                                ticket.getCreatedBy().getEmployeeId(),
                                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getEmployeeId() : null);
                return ResponseEntity.ok(ticketMapper.mapToResponse(ticket));
        }

        @GetMapping("/status/{status}")
        public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@PathVariable String status) {
                List<IncidentTicket> tickets = ticketService.getTicketsByStatus(status);
                List<TicketResponse> responses = tickets.stream()
                                .map(ticketMapper::mapToResponse)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(responses);
        }

        // ------------------- APPROVE TICKET -------------------
        @PutMapping("/{ticketId}/approve")
        @Operation(summary = "Approve a ticket", description = "Approves a ticket. Approval is irreversible and is required before assigning or closing the ticket. Only managers or administrators can approve tickets.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket approved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
        })
        @PreAuthorize("hasRole('MANAGER')")
        public ResponseEntity<TicketResponse> approveTicket(@PathVariable Long ticketId) {
                IncidentTicket approvedTicket = ticketService.approveTicket(ticketId);
                return ResponseEntity.ok(ticketMapper.mapToResponse(approvedTicket));
        }

        @Operation(summary = "Close a ticket", description = "Closes an approved ticket after resolution. Records the closing employee and timestamp.")
        @PutMapping("/{ticketId}/close")
        public ResponseEntity<TicketResponse> closeTicket(
                        @PathVariable Long ticketId,
                        @RequestParam Long closedByEmployeeId) {

                TicketResponse response = ticketService.closeTicket(ticketId, closedByEmployeeId);

                return ResponseEntity.ok(response);
        }

        // -----------------Delete Ticket ---------------
        @DeleteMapping("/{ticketId}")
        @Operation(summary = "Delete a ticket", description = "Deletes a ticket and all its associated assignments and attachments")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Ticket deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Ticket not found")
        })
        public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
                ticketService.deleteTicket(ticketId);
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/ticket/{ticketId}")
        @Operation(summary = "Delete all assignments for a ticket", description = "Deletes all ticket assignments for the given ticket ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "All assignments deleted successfully", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
        })
        public ResponseEntity<Void> deleteAssignmentsByTicket(@PathVariable Long ticketId) {
                ticketAssignmentService.deleteAssignmentsByTicket(ticketId);
                return ResponseEntity.noContent().build();
        }

        // ------------------- get assigned tickets by emp id-----------------------
        @Operation(summary = "Get tickets assigned to an employee")
        @GetMapping("/assigned/{employeeId}")
        public ResponseEntity<List<TicketResponse>> getAssignedTickets(@PathVariable Long employeeId) {

                return ResponseEntity.ok(
                                ticketService.getTicketsAssignedToEmployee(employeeId));
        }

}