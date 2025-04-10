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
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;

    // Admin login endpoint
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            System.out.println("======== " + authRequest.getUsername() +" "+ authRequest.getPassword());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null , "Incorrect username or password"));
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        // Ensure the user has admin authority
        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(null , "Access denied: not an admin"));
        }
        String jwt = jwtUtil.generateToken(userDetails.getUsername() , userDetails.getAuthorities());
        return ResponseEntity.ok(new AuthResponse(jwt , "Succesfull login"));
    }
    
    // Contest Participant (Student) login endpoint
    @PostMapping("/participant/login")
    public ResponseEntity<AuthResponse> participantLogin(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null , "Incorrect username or password"));
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        // Ensure the user has participant authority
        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PARTICIPANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(null , "Access denied: not a contest participant"));
        }
        String jwt = jwtUtil.generateToken(userDetails.getUsername() , userDetails.getAuthorities());
        return ResponseEntity.ok(new AuthResponse(jwt , "Succesfull login"));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // In a JWT stateless system, there’s usually nothing to do on the server side.
        // Optionally, you could implement token blacklisting.
        return ResponseEntity.ok("Logged out successfully");
    }
}
