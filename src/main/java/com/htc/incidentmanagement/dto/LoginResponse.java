package com.htc.incidentmanagement.dto;



public class LoginResponse {

    private String token;
    private String role;
    private Long employeeId;
    private String name;

    public LoginResponse(String token, Long employeeId, String name, String role) {
        this.token = token;
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    

    // getters
}
