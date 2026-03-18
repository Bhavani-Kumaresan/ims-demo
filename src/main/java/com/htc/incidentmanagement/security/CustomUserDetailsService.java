package com.htc.incidentmanagement.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.EmployeeAuth;
import com.htc.incidentmanagement.repository.EmployeeAuthRepository;
import com.htc.incidentmanagement.repository.EmployeeRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepo;
    private final EmployeeAuthRepository authRepo;

    public CustomUserDetailsService(EmployeeRepository employeeRepo,
            EmployeeAuthRepository authRepo) {
        this.employeeRepo = employeeRepo;
        this.authRepo = authRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        Employee emp = employeeRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        EmployeeAuth auth = authRepo.findById(emp.getEmployeeId())
                .orElseThrow(() -> new UsernameNotFoundException("Auth not found"));

        return new EmployeeUserDetails(emp, auth);
    }
}
