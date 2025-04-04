package com.shaastra.management.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shaastra.management.config.CustomUserDetailsService;
import com.shaastra.management.config.JwtUtil;
import com.shaastra.management.resource_representation.AuthRequest;
import com.shaastra.management.resource_representation.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;  // You can also configure a bean for this

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

 // Admin login endpoint
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Incorrect username or password"));
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthResponse("Access denied: not an admin"));
        }
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
    
    // Student login endpoint
    @PostMapping("/student/login")
    public ResponseEntity<AuthResponse> studentLogin(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Incorrect username or password"));
        }
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        if (userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AuthResponse("Access denied: not a student"));
        }
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
