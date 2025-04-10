package com.shaastra.management.config;


import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {
            CorsConfigurationSource source = corsConfigurationSource();
            cors.configurationSource(source);
        	})
        	.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            		// Public endpoints
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/students/register").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/contest-participants/register").permitAll()
                    // Public endpoints for contests and contest problems listings
                    // Admin endpoints
                    .requestMatchers(HttpMethod.POST , "/api/admin/**").permitAll()
                    // ContestParticipants endpoints
                    // GET all: accessible to ADMIN and PARTICIPANT if needed; adjust as required.
                    .requestMatchers(HttpMethod.GET, "/api/contest-participants").hasAnyRole("ADMIN", "PARTICIPANT")
                    // GET by id: ADMIN only
                    .requestMatchers(HttpMethod.GET, "/api/contest-participants/*").hasRole("ADMIN")
                    // POST: allowed for ADMIN and PARTICIPANT (if self-registration is allowed)  
                    .requestMatchers(HttpMethod.POST, "/api/contest-participants").hasAnyRole("ADMIN", "PARTICIPANT")
                    // PATCH: allowed for ADMIN and PARTICIPANT  
                    .requestMatchers(HttpMethod.PATCH, "/api/contest-participants/*").hasAnyRole("ADMIN", "PARTICIPANT")
                    // DELETE: ADMIN only  
                    .requestMatchers(HttpMethod.DELETE, "/api/contest-participants/**").hasRole("ADMIN")
                    // GET by contest: ADMIN only
                    .requestMatchers(HttpMethod.GET, "/api/contest-participants/by-contest/*").hasRole("ADMIN")
                    // Contests endpoints
                    // GET all contests: accessible to ADMIN and PARTICIPANT
                    .requestMatchers(HttpMethod.GET, "/api/contests").hasAnyRole("ADMIN", "PARTICIPANT")
                    // GET contest by id: ADMIN only
//                    .requestMatchers(HttpMethod.GET, "/api/contests/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/contests/recent/participants-summary").hasRole("ADMIN")

                    // POST, PUT, PATCH, DELETE: ADMIN only
                    .requestMatchers(HttpMethod.POST, "/api/contests/create-contest").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/contests/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/contests/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/contests/**").hasRole("ADMIN")
                    // GET upcoming contests: accessible to ADMIN and PARTICIPANT
                    .requestMatchers(HttpMethod.GET, "/api/contests/upcoming").hasAnyRole("ADMIN", "PARTICIPANT")
                    
                    // ContestResults endpoints
                    // GET all contest results: ADMIN only
                    .requestMatchers(HttpMethod.GET, "/api/contest-results").hasRole("ADMIN")
                    // GET contest result by id: ADMIN and PARTICIPANT
                    .requestMatchers(HttpMethod.GET, "/api/contest-results/*").hasAnyRole("ADMIN", "PARTICIPANT")
                    // GET contest-wise score: ADMIN and PARTICIPANT
                    .requestMatchers(HttpMethod.GET, "/api/contest-results/contest-wise-score/*").hasAnyRole("ADMIN", "PARTICIPANT")
                    // POST, PUT, PATCH, DELETE contest results: ADMIN only
                    .requestMatchers(HttpMethod.POST, "/api/contest-results/create-contest-result/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/contest-results/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/contest-results/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/contest-results/**").hasRole("ADMIN")
                    // GET contest ranking: ADMIN and PARTICIPANT
                    .requestMatchers(HttpMethod.GET, "/api/contest-results/contest/*/ranking").hasAnyRole("ADMIN", "PARTICIPANT")
                    
                    // ContestProblems endpoints
                    // GET all contest problems: ADMIN only
                    .requestMatchers(HttpMethod.GET, "/api/contest-problems").hasRole("ADMIN")
                    // GET contest problem by id: ADMIN only
                    .requestMatchers(HttpMethod.GET, "/api/contest-problems/*").hasRole("ADMIN")
                    // POST, PUT, PATCH, DELETE contest problems: ADMIN only
                    .requestMatchers(HttpMethod.POST, "/api/contest-problems/add-new-problems").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/contest-problems/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/contest-problems/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/contest-problems/**").hasRole("ADMIN")
                    // GET search contest problems: ADMIN and PARTICIPANT
                    .requestMatchers(HttpMethod.GET, "/api/contest-problems/search").hasAnyRole("ADMIN", "PARTICIPANT")
                    
                    // SolvedProblems endpoints:
                    .requestMatchers(HttpMethod.GET, "/api/solved-problems/participant/**").hasAnyRole("ADMIN", "PARTICIPANT")
                    .requestMatchers(HttpMethod.GET, "/api/solved-problems/contest/**/participant/**/solved-problems").hasAnyRole("ADMIN", "PARTICIPANT")
                    .requestMatchers(HttpMethod.GET, "/api/solved-problems/contest/**/participant/**/total-marks").hasAnyRole("ADMIN", "PARTICIPANT")
                    // This should be after the more specific rules
                    .requestMatchers(HttpMethod.GET, "/api/solved-problems/*").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/solved-problems/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/solved-problems/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/solved-problems/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/solved-problems/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                    
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Configure CORS to allow requests from your Angular app
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Angular development URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization")); // Allow these headers

        configuration.setAllowCredentials(true); // if using credentials like cookies
        configuration.setMaxAge((long) 3600); // Set max age for preflight cache

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    // Expose the AuthenticationManager bean for login processing
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        return authentication -> {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (passwordEncoder.matches(password, user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
            } else {
                throw new BadCredentialsException("Incorrect username or password");
            }
        };
    }
}
