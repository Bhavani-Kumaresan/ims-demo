package com.htc.incidentmanagement.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.htc.incidentmanagement.dto.LoginRequest;
import com.htc.incidentmanagement.dto.LoginResponse;
import com.htc.incidentmanagement.exception.BusinessValidationException;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.EmployeeAuth;
import com.htc.incidentmanagement.repository.EmployeeAuthRepository;
import com.htc.incidentmanagement.repository.EmployeeRepository;
import com.htc.incidentmanagement.security.JwtUtil;
import com.htc.incidentmanagement.service.AuthService;
import com.htc.incidentmanagement.service.AuthTokenService;

@Service
public class AuthServiceImpl implements AuthService {

        private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

        private final EmployeeRepository employeeRepository;
        private final EmployeeAuthRepository authRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtService;
        private final AuthTokenService authTokenService;

        public AuthServiceImpl(EmployeeRepository employeeRepository,
                        EmployeeAuthRepository authRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtService,
                        AuthTokenService authTokenService) {
                this.employeeRepository = employeeRepository;
                this.authRepository = authRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.authTokenService = authTokenService;
                logger.info("AuthService initialized.");
        }

        @Override
        public LoginResponse login(LoginRequest request) {

                Employee employee = employeeRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new BusinessValidationException("Invalid credentials"));

                EmployeeAuth auth = authRepository.findByEmployee(employee)
                                .orElseThrow(() -> new BusinessValidationException("Password not set"));

                if (!auth.getActive()) {
                        throw new BusinessValidationException("Account is inactive");
                }

                if (!passwordEncoder.matches(request.getPassword(), auth.getPassword())) {
                        throw new BusinessValidationException("Invalid credentials");
                }

                authTokenService.revokeAllTokensForUser(employee.getEmployeeId());

                String token = jwtService.generateToken(employee);

                authTokenService.saveToken(
                                employee.getEmployeeId(),
                                token,
                                jwtService.getJwtExpirationMillis());

                LoginResponse loginResponse = new LoginResponse(
                                token,
                                employee.getEmployeeId(),
                                employee.getName(),
                                employee.getRole());

                return loginResponse;
        }

        @Override
        public void setOrUpdatePassword(Long employeeId, String newPassword) {

                Employee employee = employeeRepository.findById(employeeId)
                                .orElseThrow(() -> new BusinessValidationException("Employee not found"));

                EmployeeAuth auth = authRepository.findByEmployee(employee)
                                .orElse(new EmployeeAuth(employee));

                auth.setPassword(passwordEncoder.encode(newPassword));
                auth.setActive(true);

                authRepository.save(auth);
        }

        @Override
        public void initiateForgotPassword(String email) {

                Employee employee = employeeRepository.findByEmail(email)
                                .orElseThrow(() -> new BusinessValidationException("Employee not found"));

                // For now: generate reset token
                // Later: email integration
                String resetToken = jwtService.generatePasswordResetToken(employee);

                // Log / store token / send email
                System.out.println("Password reset token: " + resetToken);
        }
}
