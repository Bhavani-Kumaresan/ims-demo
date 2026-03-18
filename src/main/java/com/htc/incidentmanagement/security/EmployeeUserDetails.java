package com.htc.incidentmanagement.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.htc.incidentmanagement.model.Employee;
import com.htc.incidentmanagement.model.EmployeeAuth;

public class EmployeeUserDetails implements UserDetails {

    private final Employee employee;
    private final EmployeeAuth auth;

    public EmployeeUserDetails(Employee employee, EmployeeAuth auth) {
        this.employee = employee;
        this.auth = auth;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + employee.getRole()));
    }

    @Override
    public String getPassword() {
        return auth.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    public Long getEmploeeId() {
        return employee.getEmployeeId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // can be enhanced later
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // can be enhanced later
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // can be enhanced later
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(auth.getActive());
    }
}
