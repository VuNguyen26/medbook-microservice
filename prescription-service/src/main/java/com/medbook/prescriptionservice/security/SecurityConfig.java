package com.medbook.prescriptionservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 🔐 Security configuration for Prescription Service
 * - Uses JWT authentication
 * - Stateless (no session)
 * - Swagger public
 * - Role-based authorization
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since using JWT
                .csrf(csrf -> csrf.disable())

                // Disable default login methods
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())

                // Stateless: each request must have its own JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 🛡Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (Swagger + Healthcheck)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**"
                        ).permitAll()

                        // Nếu có public API trong service
                        .requestMatchers("/prescriptions/public/**").permitAll()

                        // Bác sĩ và admin có quyền tạo/sửa/xóa toa thuốc
                        .requestMatchers("/prescriptions/**").hasAnyRole("DOCTOR", "ADMIN")

                        // Bệnh nhân chỉ được xem toa thuốc của mình
                        .requestMatchers("/prescriptions").hasAnyRole("DOCTOR", "PATIENT", "ADMIN")
                        .anyRequest().authenticated()
                )

                // Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
