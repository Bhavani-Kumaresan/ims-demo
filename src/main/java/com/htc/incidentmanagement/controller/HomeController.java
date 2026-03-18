package com.htc.incidentmanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "System Health", description = "API health check and welcome endpoints")
public class HomeController {

        @Operation(summary = "API Health Check & Welcome", description = "Verify API is running and accessible")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "API is healthy", content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "welcome", summary = "Welcome message", value = "Welcome to HTC Incident Management System v1.0\\n\\n🚀 API Status: Healthy")))
        })
        @GetMapping("/home")
        public String welcome() {
                return """
                                Welcome to HTC Incident Management System v1.0

                                 API Status: Healthy & Running
                                Endpoints:
                                • /api/employees
                                • /api/categories
                                • /api/slas
                                • /api/tickets
                                • /api/attachments
                                • /api/ticket-assignments

                                Swagger: /swagger-ui.html
                                OpenAPI: /v3/api-docs
                                """;
        }

        @Operation(summary = "Health Check", description = "For monitoring/load balancers")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Service healthy", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "healthy", value = "{\"status\":\"UP\",\"timestamp\":\"2026-01-07T18:47:00Z\"}")))
        })
        @GetMapping("/health")
        public String health() {
                return "{\"status\":\"UP\",\"timestamp\":\"" + java.time.Instant.now() + "\"}";
        }
}
