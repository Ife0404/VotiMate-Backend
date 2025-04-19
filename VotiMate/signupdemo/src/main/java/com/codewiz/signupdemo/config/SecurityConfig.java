package com.codewiz.signupdemo.config;

import com.codewiz.signupdemo.dto.LoginRequest;
import com.codewiz.signupdemo.service.StudentDetailsService;
import com.codewiz.signupdemo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.OncePerRequestFilter;

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
                        .requestMatchers("/api/student/**").permitAll()
                        .requestMatchers("/api/elections/**").permitAll()
                        .requestMatchers("/api/candidates/**").permitAll()
                        .requestMatchers("/api/votes/**").permitAll()
                        .requestMatchers("/api/results/**").permitAll()
                        .requestMatchers("/api/chatbot/**").permitAll() // Exclude chatbot endpoints
                        .anyRequest().authenticated()
                )
                .addFilter(new JwtLoginFilter(authenticationManager, jwtUtil))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), LogoutFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(studentDetailsService).passwordEncoder(passwordEncoder);
        return authBuilder.build();
    }

    public static class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;
        private static final Logger logger = LoggerFactory.getLogger(JwtLoginFilter.class);

        public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
            this.authenticationManager = authenticationManager;
            setFilterProcessesUrl("/api/student/login");
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
            try {
                LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
                logger.info("Parsed login request - matricNumber: {}, password: {}", loginRequest.getMatricNumber(), loginRequest.getPassword());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        loginRequest.getMatricNumber(), loginRequest.getPassword());
                logger.info("Attempting authentication for matricNumber: {}", loginRequest.getMatricNumber());
                Authentication auth = authenticationManager.authenticate(authToken);
                logger.info("Authentication result: {}", auth.isAuthenticated());
                return auth;
            } catch (IOException e) {
                logger.error("Failed to parse login request", e);
                throw new AuthenticationServiceException("Failed to parse login request", e);
            }
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
            String username = authResult.getName();
            String token = jwtUtil.generateToken(username);
            logger.info("Login successful for matric number: {}, token generated: {}", username, token);
            response.addHeader("Authorization", "Bearer " + token);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Login Successful\", \"token\": \"" + token + "\"}");
        }

        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
            logger.warn("Login failed: {}", failed.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid matric number or password\"}");
        }
    }

    public static class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;
        private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

        public JwtAuthenticationFilter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            String requestURI = request.getRequestURI();
            logger.info("Processing request: {}", requestURI);

            // Skip authentication for permitted endpoints
            if (requestURI.startsWith("/api/student") ||
                    requestURI.startsWith("/api/elections") ||
                    requestURI.startsWith("/api/candidates") ||
                    requestURI.startsWith("/api/votes") ||
                    requestURI.startsWith("/api/results") ||
                    requestURI.startsWith("/api/chatbot")) { // Add chatbot path
                logger.info("Authentication skipped for: {}", requestURI);
                chain.doFilter(request, response);
                return;
            }

            // Require token for other endpoints
            String header = request.getHeader("Authorization");
            logger.info("Authorization header: {}", header);
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    logger.info("Validated token for user: {}", username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    chain.doFilter(request, response);
                } else {
                    logger.warn("Invalid or expired token: {}", token);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"message\": \"Invalid or expired token\"}");
                }
            } else {
                logger.warn("No token provided for request: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"message\": \"Authentication token required\"}");
            }
        }
    }
}