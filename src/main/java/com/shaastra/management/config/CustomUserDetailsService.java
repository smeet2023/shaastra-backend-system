package com.shaastra.management.config;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.Admin;
import com.shaastra.management.entities.Students;
import com.shaastra.management.repositories.AdminRepository;
import com.shaastra.management.repositories.StudentsRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private StudentsRepository studentRepository; // For students
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First, try to load an admin by username:
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            return new CustomUserDetails(adminOpt.get(), "ADMIN");
        }
        // Otherwise, try to load a student by username:
        Optional<Students> studentOpt = studentRepository.findByUsername(username);
        if (studentOpt.isPresent()) {
            return new CustomUserDetails(studentOpt.get(), "STUDENT");
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
