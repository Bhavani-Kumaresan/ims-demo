package com.htc.incidentmanagement.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htc.incidentmanagement.constants.RoleConstants;
import com.htc.incidentmanagement.exception.BusinessValidationException;
import com.htc.incidentmanagement.exception.DuplicateResourceException;
import com.htc.incidentmanagement.exception.ResourceNotFoundException;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.EmployeeAuth;
import com.htc.incidentmanagement.repository.EmployeeAuthRepository;
import com.htc.incidentmanagement.repository.EmployeeRepository;
import com.htc.incidentmanagement.service.EmployeeService;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeAuthRepository employeeAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeAuthRepository employeeAuthRepository,
            PasswordEncoder passwordEncoder) {
        logger.info("EmployeeService initialized");
        this.employeeRepository = employeeRepository;
        this.employeeAuthRepository = employeeAuthRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.debug("Fetching all employees");
        List<Employee> employees = employeeRepository.findAll();
        logger.info("Retrieved {} employees", employees.size());
        return employees;
    }

    @Override
    public Employee getEmployeeById(Long id) {
        logger.debug("Fetching employee ID: {}", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Employee not found: ID={}", id);
                    return new ResourceNotFoundException("Employee", id);
                });
    }

    @Override
    public Employee createEmployee(Employee employee, String rawPassword) {
        logger.info("Creating employee: {}", employee.getEmail());

        try {
            validateEmployee(employee);
        } catch (BusinessValidationException e) {
            logger.warn("Validation failed for employee {}: {}", employee.getEmail(), e.getMessage());
            throw e;
        }

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            logger.warn("Duplicate email: {}", employee.getEmail());
            throw new DuplicateResourceException("Employee", employee.getEmail());
        }

        Employee saved = employeeRepository.save(employee);

        EmployeeAuth auth = new EmployeeAuth();
        auth.setEmployee(saved);
        auth.setPassword(passwordEncoder.encode(rawPassword));

        employeeAuthRepository.save(auth);

        logger.info("Employee created: ID={}, Email={}", saved.getEmployeeId(), saved.getEmail());
        return saved;
    }

    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        logger.info("Updating employee ID: {}", id);

        Employee existing = getEmployeeById(id);

        try {
            validateEmployee(employeeDetails);
        } catch (BusinessValidationException e) {
            logger.warn("Validation failed for update ID {}: {}", id, e.getMessage());
            throw e;
        }

        if (!existing.getEmail().equals(employeeDetails.getEmail()) &&
                employeeRepository.existsByEmail(employeeDetails.getEmail())) {
            logger.warn("Email conflict for update ID {}: {}", id, employeeDetails.getEmail());
            throw new DuplicateResourceException("Employee", employeeDetails.getEmail());
        }

        existing.setName(employeeDetails.getName());
        existing.setEmail(employeeDetails.getEmail());
        existing.setRole(employeeDetails.getRole());

        Employee updated = employeeRepository.save(existing);
        logger.info("Employee updated: ID={}", id);
        return updated;
    }

    @Override
    public void deleteEmployee(Long id) {
        logger.info("Deleting employee ID: {}", id);

        Employee existing = getEmployeeById(id);

        if (!existing.getAssignedTickets().isEmpty()) {
            logger.warn("Cannot delete employee ID {} - {} assigned tickets", id, existing.getAssignedTickets().size());
            throw new BusinessValidationException("Cannot delete employee with assigned tickets");
        }

        employeeRepository.delete(existing);
        logger.info("Employee deleted: ID={}", id);
    }

    @Override
    public List<Employee> getEmployeesByRole(String role) {
        logger.debug("Fetching employees by role: {}", role);
        List<Employee> employees = employeeRepository.findByRole(role.toUpperCase());
        logger.info("Found {} employees with role: {}", employees.size(), role);
        return employees;
    }

    @Override
    public boolean existsByEmail(String email) {
        boolean exists = employeeRepository.existsByEmail(email);
        logger.trace("Email exists check '{}' = {}", email, exists);
        return exists;
    }

    private void validateEmployee(Employee employee) {
        logger.debug("Validating employee: {}", employee.getEmail());

        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new BusinessValidationException("Name is required");
        }

        if (employee.getEmail() == null || !employee.getEmail().contains("@")) {
            throw new BusinessValidationException("Valid email is required");
        }

        if (validateRole(employee.getRole()).isEmpty() || validateRole(employee.getRole()).isBlank()) {
            throw new BusinessValidationException("Valid Role is required");
        }
    }

    @Override
    public Employee updateEmployeeRole(Long employeeId, String role) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        String validatedRole = validateRole(role);
        employee.setRole(validatedRole);

        return employeeRepository.save(employee);
    }

    // --------- Helper methods -------------------

    private String validateRole(String role) {

        if (role == null || role.isBlank()) {
            throw new BusinessValidationException("Role is required");
        }

        String normalizedRole = role.toUpperCase();

        if (!RoleConstants.ALLOWED_ROLES.contains(normalizedRole)) {
            throw new BusinessValidationException(
                    "Invalid role: " + role +
                            ". Allowed roles: " + RoleConstants.ALLOWED_ROLES);
        }

        return normalizedRole;
    }
}
