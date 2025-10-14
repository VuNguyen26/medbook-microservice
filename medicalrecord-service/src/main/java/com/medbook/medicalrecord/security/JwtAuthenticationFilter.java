package com.medbook.medicalrecord.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🚫 Bỏ qua filter cho các API public hoặc Swagger
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("⚠️ No JWT token found for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                String role = jwtUtil.extractRole(token);

                List<SimpleGrantedAuthority> authorities =
                        (role != null)
                                ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                                : Collections.emptyList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ✅ Đặt Authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("✅ Authenticated user: {} with role: {}", username, role);
            } else {
                log.warn("❌ Invalid or expired JWT token");
            }
        } catch (Exception e) {
            log.error("❌ JWT validation failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        // ✅ Đổi path public cho đúng service hiện tại
        return path.startsWith("/api/medicalrecords/public")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/")
                || path.equals("/error");
    }
}
