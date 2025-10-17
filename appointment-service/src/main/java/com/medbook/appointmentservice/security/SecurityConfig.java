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
                // Tắt CSRF (chỉ dùng JWT)
                .csrf(csrf -> csrf.disable())

                // Stateless session – không lưu session server-side
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Quy tắc phân quyền
                .authorizeHttpRequests(auth -> auth
                        // Cho phép public endpoint (Swagger + test)
                        .requestMatchers(
                                "/api/appointments/public/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Cho phép bệnh nhân hoặc bác sĩ tạo lịch hẹn
                        .requestMatchers(HttpMethod.POST, "/api/appointments/**").hasAnyRole("PATIENT", "DOCTOR")

                        // Cho phép bác sĩ hoặc admin cập nhật / xóa lịch hẹn
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasAnyRole("DOCTOR", "ADMIN")

                        // Cho phép tất cả role (bác sĩ, bệnh nhân, admin) xem lịch hẹn
                        .requestMatchers(HttpMethod.GET, "/api/appointments/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")

                        // Các request khác cần xác thực
                        .anyRequest().authenticated()
                )

                // Thêm filter JWT để kiểm tra token trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
