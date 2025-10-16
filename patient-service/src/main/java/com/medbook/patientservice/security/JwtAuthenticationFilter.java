package com.medbook.patientservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        logger.info("➡️ [Filter] Request URI: {}", path);

        // Bỏ qua filter cho các API public
        if (isPublicPath(path)) {
            logger.debug("🟢 Public path detected: {}, skip JWT validation", path);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("❌ No Authorization header or invalid format for URI: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        logger.debug("🔑 JWT token received: {}", token);

        try {
            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);
                String username = claims.getSubject();
                String role = jwtUtil.extractRole(token);

                logger.info("Token valid. User: {}, Role: {}", username, role);

                List<SimpleGrantedAuthority> authorities =
                        (role != null)
                                ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                                : Collections.emptyList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set in context for user: {}", username);
            } else {
                logger.warn("Invalid or expired JWT token");
            }
        } catch (Exception e) {
            logger.error("Error while validating JWT: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        boolean isPublic = path.startsWith("/api/patients/public")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/")
                || path.equals("/error");
        if (isPublic) logger.debug("🟢 Path '{}' is public", path);
        return isPublic;
    }
}
