package com.htc.incidentmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.htc.incidentmanagement.dto.CreateAssignmentRequest;

import com.htc.incidentmanagement.dto.TicketAssignmentResponse;
import com.htc.incidentmanagement.model.TicketAssignment;
import com.htc.incidentmanagement.service.TicketAssignmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ticket-comments")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ticket Assignments", description = "Ticket reassignment history and current assignee management")
public class TicketCommentController {

        private final TicketAssignmentService ticketAssignmentService;

        public TicketCommentController(TicketAssignmentService ticketAssignmentService) {
                this.ticketAssignmentService = ticketAssignmentService;
        }

        @Operation(summary = "Get ticket assignment history", description = "Retrieves the complete assignment history of a ticket, including previous assignees and timestamps.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Assignment history retrieved", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TicketAssignmentResponse.class)))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
        })
        @GetMapping("/ticket/{ticketId}")
        public ResponseEntity<List<TicketAssignmentResponse>> getByTicket(@PathVariable Long ticketId) {
                List<TicketAssignmentResponse> comments = ticketAssignmentService.getAssignmentsByTicket(ticketId);
                return ResponseEntity.ok(comments);
        }

        @Operation(summary = "Create new ticket assignment", description = "Assigns a ticket to an employee/agent creating a new assignment record. Updates the ticket's current assignee and records assignment timestamp/comment.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"ticketId\": 101, \"employeeId\": 2, \"comment\": \"Assigning to network team\" }"))))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Assignment created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketAssignment.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket or employee not found", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
        })
        @PostMapping
        public ResponseEntity<TicketAssignmentResponse> create(@Valid @RequestBody CreateAssignmentRequest request) {
                TicketAssignmentResponse created = ticketAssignmentService.createAssignment(
                                request.getTicketId(), request.getEmployeeId(), request.getComment());
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

}
