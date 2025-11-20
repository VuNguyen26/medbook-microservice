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
            AuthenticationFilter authenticationFilter // ✔ Inject filter
    ) {

        String frontendRedirect = "http://localhost:5173/login/success";
        String frontendFailure = "http://localhost:5173/login?error=true";

        // OAuth2 success handler
        ServerAuthenticationSuccessHandler successHandler = (exchange, authentication) -> {
            OAuth2User user = (OAuth2User) authentication.getPrincipal();
            String email = user.getAttribute("email");
            String name  = user.getAttribute("name");

            System.out.println("OAuth2 success: " + email);

            return webClientBuilder.build()
                    .post()
                    .uri("lb://user-service/auth/oauth2/sync")
                    .bodyValue(Map.of("email", email, "name", name))
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(token -> {
                        if (token == null || token.isBlank()) {
                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(frontendFailure));
                            return exchange.getExchange().getResponse().setComplete();
                        }

                        String redirectUrl = frontendRedirect +
                                "?token=" + token +
                                "&email=" + email +
                                "&role=PATIENT";

                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(redirectUrl));
                        return exchange.getExchange().getResponse().setComplete();
                    })
                    .onErrorResume(e -> {
                        System.err.println("OAuth2 sync error: " + e.getMessage());
                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(frontendFailure));
                        return exchange.getExchange().getResponse().setComplete();
                    });
        };

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())

                // ⭐ THÊM FILTER JWT VÀO CHUỖI BẢO MẬT ⭐
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(
                                "/api/auth/**",
                                "/actuator/**",
                                "/login/**",
                                "/oauth2/**"
                        ).permitAll()
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
                "http://localhost:5173",
                "http://localhost:5174",
                "http://172.18.0.1:5173",
                "http://host.docker.internal:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
