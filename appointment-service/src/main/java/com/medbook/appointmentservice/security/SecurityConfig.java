package com.medbook.appointmentservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ===== PUBLIC APIS =====
                        .requestMatchers(
                                "/appointments/public/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // ===== PUBLIC QR Check-in =====
                        .requestMatchers(HttpMethod.GET, "/appointments/*/qr").permitAll()

                        // ===== PUBLIC SLOT =====
                        .requestMatchers(HttpMethod.GET, "/appointments/slots/**").permitAll()

                        // ===== INTERNAL PAID CALLBACK (Payment Service) =====
                        // Không yêu cầu JWT — đây là API nội bộ giữa microservices
                        .requestMatchers(HttpMethod.PUT, "/appointments/*/paid").permitAll()

                        // ===== CREATE APPOINTMENT =====
                        .requestMatchers(HttpMethod.POST, "/appointments/**")
                        .hasAnyRole("PATIENT", "DOCTOR")

                        // ===== UPDATE / DELETE OTHER DATA =====
                        .requestMatchers(HttpMethod.PUT, "/appointments/**")
                        .hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/appointments/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        // ===== GET APPOINTMENTS =====
                        .requestMatchers(HttpMethod.GET, "/appointments/**")
                        .hasAnyRole("PATIENT", "DOCTOR", "ADMIN")

                        // ===== EVERYTHING ELSE =====
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
