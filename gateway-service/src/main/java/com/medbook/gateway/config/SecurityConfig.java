package com.medbook.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public AuthenticationFilter authenticationFilter(JwtUtil jwtUtil) {
        return new AuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            WebClient.Builder webClientBuilder,
            AuthenticationFilter authenticationFilter,
            CorsConfigurationSource corsConfigurationSource
    ) {

        String frontendRedirect = "http://localhost:5173/login/success";
        String frontendFailure  = "http://localhost:5173/login?error=true";

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Cho phép OPTIONS (preflight) đi qua luôn
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                )

                // Đặt JWT filter
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .authorizeExchange(ex -> ex

                        // ===== PUBLIC ROUTES =====
                        .pathMatchers(
                                "/api/doctors/**",
                                "/api/specialties/**",
                                "/api/auth/**",
                                "/login/**",
                                "/oauth2/**",
                                "/actuator/**",
                                "/api/appointments/slots/**",
                                "/api/appointments/*/qr",
                                "/api/payments/fake"
                        ).permitAll()

                        // ===== PATIENT ONLY =====
                        .pathMatchers(HttpMethod.POST, "/api/appointments").hasAuthority("PATIENT")
                        .pathMatchers(HttpMethod.POST, "/api/payments").hasAuthority("PATIENT")
                        .pathMatchers(HttpMethod.POST, "/api/payments/momo").hasAuthority("PATIENT")

                        // All others must login
                        .anyExchange().authenticated()
                )

                // OAuth2 login
                .oauth2Login(oauth -> oauth
                        .authenticationSuccessHandler((exchange, authentication) -> {

                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                            String email = oauthUser.getAttribute("email");
                            String name  = oauthUser.getAttribute("name");

                            return webClientBuilder.build()
                                    .post()
                                    .uri("lb://user-service/auth/oauth2/sync")
                                    .bodyValue(Map.of("email", email, "name", name))
                                    .retrieve()
                                    .bodyToMono(Map.class)
                                    .flatMap(data -> {

                                        String token   = (String)  data.get("token");
                                        Integer userId = (Integer) data.get("id");
                                        String role    = (String)  data.get("role");
                                        String userName= (String)  data.get("name");

                                        String redirectUrl = frontendRedirect
                                                + "?token=" + token
                                                + "&id=" + userId
                                                + "&email=" + email
                                                + "&name=" + userName
                                                + "&role=" + role;

                                        var response = exchange.getExchange().getResponse();
                                        response.setStatusCode(HttpStatus.FOUND);
                                        response.getHeaders().setLocation(URI.create(redirectUrl));
                                        return response.setComplete();
                                    })
                                    .onErrorResume(e -> {
                                        var response = exchange.getExchange().getResponse();
                                        response.setStatusCode(HttpStatus.FOUND);
                                        response.getHeaders().setLocation(URI.create(frontendFailure));
                                        return response.setComplete();
                                    });
                        })
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                            byte[] bytes = "{\"message\":\"Unauthorized\"}".getBytes(StandardCharsets.UTF_8);
                            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                        })
                        .accessDeniedHandler((exchange, e) -> {
                            var response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.FORBIDDEN);
                            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                            byte[] bytes = "{\"message\":\"Access Denied\"}".getBytes(StandardCharsets.UTF_8);
                            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                        })
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");

        // BẮT BUỘC PHẢI CÓ – CHO PHÉP POST, OPTIONS, PUT, DELETE
        config.addAllowedMethod("*");

        // Expose headers cho FE đọc JWT hoặc redirect nếu cần
        config.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
