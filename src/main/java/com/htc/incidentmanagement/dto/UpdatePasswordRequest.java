package com.htc.incidentmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdatePasswordRequest {

    @NotBlank(message = "password is required")
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    // getter & setter
}
