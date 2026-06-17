package com.idcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 *
 * Current setup: Stateless, open API for development/demo purposes.
 * To enable full authentication, add JWT filter here.
 *
 * Public paths:
 *   - /api/**           → all REST endpoints (open for now)
 *   - /swagger-ui/**   → Swagger UI
 *   - /v3/api-docs/**  → OpenAPI JSON
 *   - /uploads/**      → uploaded photos served statically
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API (stateless)
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session — no cookies
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public: Swagger UI & OpenAPI spec
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                // Public: static uploads
                .requestMatchers("/uploads/**").permitAll()
                // Public: all API endpoints (open for demo; restrict per endpoint in production)
                .requestMatchers("/api/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
