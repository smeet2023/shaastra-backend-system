package com.shaastra.management.config;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    // Use a strong key in production, possibly injected from properties
    private final String SECRET_KEY = "YourSecretKeyForJWTGenerationChangeMe";
    // Extract username (here, we assume it's the admin's username) from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // Extract expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    // Generic method to extract a claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(SECRET_KEY.getBytes())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    // Generate token for admin user (subject = username)
    public String generateToken(String username , Collection<? extends GrantedAuthority> authorities) {
    	Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, username);
    }
    private String createToken(Map<String, Object> claims, String subject) {
    	System.out.println("hey !!!!!!!!!!!!!! " + subject);
        long expirationTime = 1000 * 60 * 60 * 10; // 10 hours
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }
    // Validate token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
