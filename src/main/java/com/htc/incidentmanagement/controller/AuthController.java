package com.htc.incidentmanagement.controller;

import com.htc.incidentmanagement.dto.*;
import com.htc.incidentmanagement.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.htc.incidentmanagement.service.AuthTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login & Logout module.")
public class AuthController {

    private final AuthService authService;
    private final AuthTokenService authTokenService;

    public AuthController(AuthService authService, AuthTokenService authTokenService) {
        this.authService = authService;
        this.authTokenService = authTokenService;
    }

    // -------------------- LOGIN --------------------
    @Operation(summary = "Employee login", description = "Authenticates an employee using email and password and returns a JWT access token. Only active employees are allowed to log in.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // -------------------- SET / UPDATE PASSWORD --------------------
    @Operation(summary = "Set or update password for employee", description = "Allows an existing employee to set or update their password. Intended for first-time password setup or admin-initiated resets.")
    @PutMapping("/employees/{employeeId}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long employeeId,
            @RequestBody UpdatePasswordRequest request) {

        authService.setOrUpdatePassword(employeeId, request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    // -------------------- FORGOT PASSWORD --------------------
    @Operation(summary = "Forgot password", description = "Allows an employee to reset their password after identity verification. Existing tokens are invalidated after reset.")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.initiateForgotPassword(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    // ================== Auth Token methods==================

    // -------------------- VALIDATE TOKEN --------------------
    @Operation(summary = "Validate JWT token")
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(
            @RequestParam String token) {

        return ResponseEntity.ok(authTokenService.isTokenValid(token));
    }

    // -------------------- LOGOUT --------------------
    @Operation(summary = "Logout and revoke token", description = "Revokes the current JWT token and prevents further access using the same token.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestParam String token) {

        authTokenService.revokeToken(token);
        return ResponseEntity.noContent().build();
    }

    // -------------------- FORCE LOGOUT USER --------------------
    @Operation(summary = "Revoke all tokens for a user (Admin)", description = "Revokes the current JWT token and prevents further access using the same token.")
    @PostMapping("/revoke/user/{userId}")
    public ResponseEntity<Void> revokeAllTokens(
            @PathVariable Long userId) {

        authTokenService.revokeAllTokensForUser(userId);
        return ResponseEntity.noContent().build();
    }
}
