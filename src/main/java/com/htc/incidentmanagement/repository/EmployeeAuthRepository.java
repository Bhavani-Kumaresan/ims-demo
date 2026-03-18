package com.htc.incidentmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.EmployeeAuth;

@Repository
public interface EmployeeAuthRepository extends JpaRepository<EmployeeAuth, Long> {

    Optional<EmployeeAuth> findByEmployee(Employee employee);

}
