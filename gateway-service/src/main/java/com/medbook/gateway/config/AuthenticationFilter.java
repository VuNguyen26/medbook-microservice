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

        // ============================================================
        // QR CHECKIN PUBLIC
        // ============================================================
        if (path.matches("^/appointments/\\d+/qr$") ||
                path.matches("^/api/appointments/\\d+/qr$")) {

            System.out.println(">>> PUBLIC QR (skip JWT): " + path);
            return chain.filter(exchange);
        }

        // ============================================================
        // ⭐⭐⭐ THÊM REVIEW API VÀO PUBLIC PATTERN ⭐⭐⭐
        // ============================================================
        if (path.matches("^/api/appointments/doctor/\\d+/reviews$")) {
            System.out.println(">>> PUBLIC REVIEW (skip JWT): " + path);
            return chain.filter(exchange);
        }

        // Public routes không yêu cầu JWT
        String[] PUBLIC_PATTERNS = {
                "^/api/doctors(/.*)?$",
                "^/api/specialties(/.*)?$",
                "^/api/auth(/.*)?$",
                "^/login(/.*)?$",
                "^/oauth2(/.*)?$",
                "^/actuator(/.*)?$",
                "^/api/appointments/slots(/.*)?$",
                "^/api/appointments/reports(/.*)?$",
                "^/api/payments/fake$",
                "^/api/payments/fake(/.*)?$"
        };

        for (String pattern : PUBLIC_PATTERNS) {
            if (path.matches(pattern)) {
                System.out.println(">>> PUBLIC ROUTE (skip JWT): " + path);
                return chain.filter(exchange);
            }
        }

        // ============================================================
        // JWT bắt buộc cho các API còn lại
        // ============================================================
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

        String username = claims.getSubject();
        String role = (String) claims.get("role");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority(role)));

        System.out.println(">>> JWT AUTHORIZED: " + username + " | ROLE = " + role);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}
