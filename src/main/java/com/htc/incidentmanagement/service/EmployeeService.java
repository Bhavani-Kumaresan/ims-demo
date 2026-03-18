package com.htc.incidentmanagement.service;

import java.util.List;

import com.htc.incidentmanagement.model.Employee;

public interface EmployeeService {

  List<Employee> getAllEmployees();

  Employee getEmployeeById(Long id);

  Employee createEmployee(Employee employee, String rawPassword);

  Employee updateEmployee(Long id, Employee employeeDetails);

  void deleteEmployee(Long id);

  List<Employee> getEmployeesByRole(String role);

  boolean existsByEmail(String email);

  Employee updateEmployeeRole(Long employeeId, String role);
}
