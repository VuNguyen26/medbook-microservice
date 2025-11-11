package com.medbook.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
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
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         WebClient.Builder webClientBuilder) {

        String frontendRedirect = "http://localhost:5173/login/success";
        String frontendFailure = "http://localhost:5173/login?error=true";

        // Xử lý khi đăng nhập OAuth2 thành công
        ServerAuthenticationSuccessHandler successHandler = (exchange, authentication) -> {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            System.out.println("OAuth2 success: " + email);

            // Gọi user-service QUA EUREKA để sync user và tạo JWT
            return webClientBuilder.build()
                    .post()
                    .uri("lb://user-service/auth/oauth2/sync")
                    .bodyValue(Map.of("email", email, "name", name))
                    .retrieve()
                    .bodyToMono(String.class) // user-service trả về chuỗi token
                    .flatMap(token -> {
                        if (token == null || token.isBlank()) {
                            System.err.println("Không nhận được token từ user-service!");
                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(frontendFailure));
                            return exchange.getExchange().getResponse().setComplete();
                        }

                        System.out.println("JWT từ user-service: " + token);

                        // Redirect về FE cùng token + email + role
                        String redirectUrl = frontendRedirect
                                + "?token=" + token
                                + "&email=" + email
                                + "&role=PATIENT";

                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(redirectUrl));
                        return exchange.getExchange().getResponse().setComplete();
                    })
                    .onErrorResume(err -> {
                        System.err.println("OAuth2 Sync lỗi: " + err.getMessage());
                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(frontendFailure));
                        return exchange.getExchange().getResponse().setComplete();
                    });
        };

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
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
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler)
                        .authenticationFailureHandler((ex, e) -> {
                            ex.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            ex.getExchange().getResponse().getHeaders().setLocation(URI.create(frontendFailure));
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

    // Cho phép FE gọi Gateway trong môi trường dev
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://host.docker.internal:5173",
                "http://172.18.0.1:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
