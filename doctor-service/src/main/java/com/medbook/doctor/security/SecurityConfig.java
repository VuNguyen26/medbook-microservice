package com.medbook.doctor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.disable())
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                // ⭐ PUBLIC API — FE ĐANG GỌI
                                                                "/doctors",
                                                                "/doctors/",
                                                                "/doctors/**", // ⭐ quan trọng

                                                                // Public endpoint lấy bác sĩ theo email
                                                                "/doctors/email/**",
                                                                "/api/doctors/email/**",

                                                                "/doctors/public/**",
                                                                "/api/doctors/public/**",

                                                                // swagger
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Cho phép actuator cho healthcheck Docker
                                                .requestMatchers(
                                                                "/actuator/**",
                                                                "/error")
                                                .permitAll()

                                                // Các API khác cần JWT
                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
