package com.htc.incidentmanagement.service;

import com.htc.incidentmanagement.dto.LoginRequest;
import com.htc.incidentmanagement.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void setOrUpdatePassword(Long employeeId, String newPassword);

    void initiateForgotPassword(String email);
}
