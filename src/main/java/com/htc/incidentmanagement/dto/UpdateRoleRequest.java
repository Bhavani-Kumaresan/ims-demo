package com.htc.incidentmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to update employee role")
public record UpdateRoleRequest(
        @Schema(example = "MANAGER") @NotBlank String role) {
}
