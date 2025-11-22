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

        // ⭐ PUBLIC PATTERNS (DÙNG REGEX ĐỂ MATCH CHÍNH XÁC MỌI TRƯỜNG HỢP)
        String[] PUBLIC_PATTERNS = {
                "^/api/doctors(/.*)?$",
                "^/api/specialties(/.*)?$",
                "^/api/auth(/.*)?$",
                "^/login(/.*)?$",
                "^/oauth2(/.*)?$",
                "^/actuator(/.*)?$"
        };

        // ⭐ Nếu path match PUBLIC → BỎ QUA JWT FILTER ⭐
        for (String pattern : PUBLIC_PATTERNS) {
            if (path.matches(pattern)) {
                System.out.println(">>> PUBLIC MATCHED: " + pattern);
                return chain.filter(exchange);
            }
        }

        // ⭐ Các route dưới đây yêu cầu JWT ⭐
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

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        Authentication auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        System.out.println(">>> Authenticated User = " + username + " | Role = " + role);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}
