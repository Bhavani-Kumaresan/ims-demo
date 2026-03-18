package com.htc.incidentmanagement.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static EmployeeUserDetails getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (auth == null ||
                !(auth.getPrincipal() instanceof EmployeeUserDetails)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated");
        }

        return (EmployeeUserDetails) auth.getPrincipal();
    }
}
