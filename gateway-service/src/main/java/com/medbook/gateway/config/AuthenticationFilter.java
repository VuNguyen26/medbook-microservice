package com.medbook.gateway.config;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        System.out.println(">>> JWT Filter Activated: " + path);

        // Allow public routes
        if (path.startsWith("/api/auth")
                || path.startsWith("/actuator")
                || path.startsWith("/login")
                || path.startsWith("/oauth2")) {
            return chain.filter(exchange);
        }

        // Check Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        Claims claims;
        try {
            claims = jwtUtil.extractAllClaims(token);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract username and role
        String username = claims.getSubject();
        String role = (String) claims.get("role");

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        Authentication auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        System.out.println(">>> Authenticated User = " + username + " | Role = " + role);

        // Attach Authentication vào SecurityContext
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}
