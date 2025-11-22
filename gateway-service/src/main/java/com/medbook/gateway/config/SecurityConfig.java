package com.medbook.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
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
            AuthenticationFilter authenticationFilter
    ) {

        String frontendRedirect = "http://localhost:5173/login/success";
        String frontendFailure = "http://localhost:5173/login?error=true";

        // OAuth2 success callback
        ServerAuthenticationSuccessHandler successHandler = (exchange, authentication) -> {
            OAuth2User user = (OAuth2User) authentication.getPrincipal();
            String email = user.getAttribute("email");
            String name  = user.getAttribute("name");

            return webClientBuilder.build()
                    .post()
                    .uri("lb://user-service/auth/oauth2/sync")
                    .bodyValue(Map.of("email", email, "name", name))
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(token -> {
                        String redirectUrl = frontendRedirect +
                                "?token=" + token +
                                "&email=" + email +
                                "&role=PATIENT";

                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(redirectUrl));
                        return exchange.getExchange().getResponse().setComplete();
                    })
                    .onErrorResume(e -> {
                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(frontendFailure));
                        return exchange.getExchange().getResponse().setComplete();
                    });
        };

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // ⭐ ĐÚNG – JWT FILTER CHẠY SAU CORS, TRƯỚC AUTHORIZATION
                .addFilterBefore(authenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION)

                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ⭐ PUBLIC ROUTES – CHO FE GỌI KHÔNG CẦN TOKEN
                        .pathMatchers(
                                "/api/doctors/**",
                                "/api/specialties/**",
                                "/api/auth/**",
                                "/login/**",
                                "/oauth2/**",
                                "/actuator/**"
                        ).permitAll()

                        // Các API khác cần token
                        .anyExchange().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .authenticationSuccessHandler(successHandler)
                        .authenticationFailureHandler((req, e) -> {
                            req.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            req.getExchange().getResponse().getHeaders().setLocation(URI.create(frontendFailure));
                            return Mono.empty();
                        })
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, e) -> {
                            req.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            req.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                            byte[] bytes = "{\"message\":\"Unauthorized\"}".getBytes(StandardCharsets.UTF_8);
                            return req.getResponse().writeWith(
                                    Mono.just(req.getResponse().bufferFactory().wrap(bytes))
                            );
                        })
                        .accessDeniedHandler((req, e) -> {
                            req.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            req.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                            byte[] bytes = "{\"message\":\"Access Denied\"}".getBytes(StandardCharsets.UTF_8);
                            return req.getResponse().writeWith(
                                    Mono.just(req.getResponse().bufferFactory().wrap(bytes))
                            );
                        })
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173"
        ));
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
