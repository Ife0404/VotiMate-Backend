package com.codewiz.signupdemo.config;

import com.codewiz.signupdemo.dto.LoginRequest;
import com.codewiz.signupdemo.service.StudentDetailsService;
import com.codewiz.signupdemo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter; // CHANGE: Added for filter ordering
import org.springframework.web.filter.OncePerRequestFilter; // CHANGE: Added for new superclass

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentDetailsService studentDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Explicitly allow all methods under /api/student/**
                        .requestMatchers("/api/student/**").permitAll()
                        // Specific authenticated endpoints
                        .requestMatchers("/api/elections/**", "/api/candidates/**").authenticated()
                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                // CHANGE: Split filters for clarity
                .addFilter(new JwtLoginFilter(authenticationManager, jwtUtil)) // For login
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), LogoutFilter.class); // For token validation
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(studentDetailsService).passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }

    // CHANGE: New filter for login processing
    public static class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;

        public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
            this.authenticationManager = authenticationManager;
            setFilterProcessesUrl("/api/student/login");
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
            try {
                LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        loginRequest.getMatricNumber(), loginRequest.getPassword());
                return authenticationManager.authenticate(authToken);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse login request", e);
            }
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            String username = authResult.getName();
            String token = jwtUtil.generateToken(username);
            response.addHeader("Authorization", "Bearer " + token);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Login Successful\", \"token\": \"" + token + "\"}");
        }
    }

    // CHANGE: New filter for token validation
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;

        public JwtAuthenticationFilter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            if (request.getRequestURI().startsWith("/api/student/")) {
                chain.doFilter(request, response);
                return;
            }
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            chain.doFilter(request, response);
        }
    }
}