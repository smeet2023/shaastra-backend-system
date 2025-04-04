package com.shaastra.management.config;


import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shaastra.management.entities.Admin;
import com.shaastra.management.entities.Students;

public class CustomUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Admin admin, String role) {
        this.username = admin.getUsername();
        this.password = admin.getPassword();
        this.authorities = List.of(() -> "ROLE_" + role);
    }

    public CustomUserDetails(Students student, String role) {
        this.username = student.getUsername();
        this.password = student.getPassword();
        this.authorities = List.of(() -> "ROLE_" + role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

