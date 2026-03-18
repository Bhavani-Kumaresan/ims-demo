package com.htc.incidentmanagement.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        JSONObject responseJson = new JSONObject();
        responseJson.put("timestamp", LocalDateTime.now().toString());
        responseJson.put("status", HttpStatus.FORBIDDEN.value());
        responseJson.put("error", HttpStatus.FORBIDDEN.getReasonPhrase());
        responseJson.put("message", "You are not authorized to perform this action");
        responseJson.put("path", request.getRequestURI());

        response.getWriter().write(responseJson.toString());
    }
}
