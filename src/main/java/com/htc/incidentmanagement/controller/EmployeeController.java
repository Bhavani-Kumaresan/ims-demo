package com.htc.incidentmanagement.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.htc.incidentmanagement.dto.CreateEmployeeRequest;
import com.htc.incidentmanagement.dto.EmployeeListResponse;
import com.htc.incidentmanagement.dto.EmployeeSummary;
import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employee Management", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Operation(summary = "Fetch all employees", description = "Retrieves a list of all employees in the system. Restricted to administrators.")
    @GetMapping("/getAllEmployees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeListResponse> getAllEmployees(@RequestParam(required = false) String role, HttpSession session) {
        logger.info("GET /employees?role={}", role);
        List<Employee> employees = role != null
                ? employeeService.getEmployeesByRole(role)
                : employeeService.getAllEmployees();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Create a new employee", description = "Creates a new employee profile and securely stores authentication credentials. This operation is restricted to administrators.")
    @PostMapping("/create")
    public ResponseEntity<EmployeeSummary> createEmployee(@RequestBody @Valid CreateEmployeeRequest request, HttpSession session) {
        logger.info("POST /employees/create - Email: {}", request.getEmail());
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());

        Employee created = employeeService.createEmployee(employee, request.getPassword());
        logger.info("Employee created successfully: ID={}", created.getEmployeeId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmployeeSummary(created));
    }

    @Operation(summary = "Get employee by ID", description = "Returns complete employee profile excluding circular references")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeSummary.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    @GetMapping("/getEmployeeById/{id}")
    public ResponseEntity<EmployeeSummary> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(new EmployeeSummary(employee));
    }

    @Operation(summary = "Update employee details", description = "Updates name, email, or role for existing employee. Email must remain unique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeSummary.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email conflict", content = @Content)
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeSummary> updateEmployee(
            @PathVariable Long id,
            @RequestBody CreateEmployeeRequest request, HttpSession session) {

        Employee employee = employeeService.getEmployeeById(id);
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());

        Employee updated = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(new EmployeeSummary(updated));
    }

    @Operation(summary = "Delete employee", description = "Permanently removes employee. Cannot delete if tickets assigned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot delete - tickets assigned", content = @Content)
    })
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, HttpSession session) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Update employee - 'ROLE'

    @Operation(summary = "Update employee role", description = "Updates employee role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeSummary.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email conflict", content = @Content)
    })
    @PutMapping("/{employeeId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeSummary> updateRole(
            @PathVariable Long employeeId,
            @RequestParam String role, HttpSession session) {

        Employee updated = employeeService.updateEmployeeRole(employeeId, role);
        return ResponseEntity.ok(new EmployeeSummary(updated));
    }
}
