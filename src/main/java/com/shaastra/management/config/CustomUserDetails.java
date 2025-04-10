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

    // Constructor for Admin users (unchanged)
    public CustomUserDetails(Admin admin) {
        this.username = admin.getUsername();
        System.out.println("@@@   admin " + this.username);
        this.password = admin.getPassword();
        this.authorities = List.of(() -> "ROLE_ADMIN");
    }

    // Constructor for Contest Participants (students)
    public CustomUserDetails(Students student) {
        // Use the student's sh_id as the username
        this.username = student.getSh_id();
        // Use the coding_contest_password field (should be stored hashed)
        this.password = student.getCoding_contest_password();
        this.authorities = List.of(() -> "ROLE_PARTICIPANT");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}

