package com.medbook.medicalrecord.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Vô hiệu hóa CSRF vì microservice không dùng session
                .csrf(csrf -> csrf.disable())

                // 🟢 Stateless: mỗi request được xác thực bằng JWT, không lưu session
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Cho phép các endpoint public, test, swagger tự do truy cập
                        .requestMatchers(
                                "/medicalrecords/test",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Cho phép bệnh nhân hoặc bác sĩ xem hồ sơ
                        .requestMatchers(HttpMethod.GET, "/medicalrecords/**")
                        .hasAnyRole("PATIENT", "DOCTOR")

                        // Chỉ bác sĩ mới được thêm/sửa/xóa hồ sơ
                        .requestMatchers(HttpMethod.POST, "/medicalrecords/**")
                        .hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/medicalrecords/**")
                        .hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/medicalrecords/**")
                        .hasRole("DOCTOR")

                        // Các request khác yêu cầu JWT hợp lệ
                        .anyRequest().authenticated()
                )

                // Gắn JWT filter vào trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
