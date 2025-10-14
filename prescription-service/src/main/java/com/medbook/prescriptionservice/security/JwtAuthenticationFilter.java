package com.medbook.prescriptionservice.security;

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

/**
 * Bộ lọc kiểm tra JWT cho mọi request vào prescription-service.
 * Nếu token hợp lệ → xác thực user trong SecurityContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // ✅ Import đúng package com.medbook.prescriptionservice.security.JwtUtil

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🧩 Log kiểm tra filter có hoạt động không
        log.info("🧩 JwtAuthenticationFilter ACTIVATED for path: {}", path);

        // 🚫 Bỏ qua các endpoint public hoặc Swagger
        if (isPublicPath(path)) {
            log.debug("➡️ Public path detected, skipping JWT check for: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // 🔍 Lấy token từ header Authorization
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.warn("⚠️ No JWT token found in request header for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // cắt "Bearer "

        try {
            // ✅ Kiểm tra token hợp lệ
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
                log.info("✅ Authenticated user: {} with role: {}", username, role);
            } else {
                log.warn("❌ Invalid or expired JWT token for path: {}", path);
            }
        } catch (Exception e) {
            log.error("💥 JWT validation failed for path {}: {}", path, e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        // 🔁 Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }

    /**
     * Định nghĩa các endpoint không cần JWT xác thực
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/prescriptions/public")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/")
                || path.equals("/error");
    }
}
