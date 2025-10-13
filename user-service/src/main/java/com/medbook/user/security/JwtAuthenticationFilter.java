package com.medbook.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Kiểm tra URL để bỏ qua các endpoint public
        String path = request.getServletPath();
        System.out.println("[JWT Filter] Request Path: " + path);

        if (isPublicPath(path)) {
            System.out.println("[JWT Filter] Skipping filter for: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy Authorization header
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT Filter] No Bearer token found → continue without auth");
            filterChain.doFilter(request, response);
            return;
        }

        // Giải mã token
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        // Kiểm tra token hợp lệ
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("[JWT Filter] Token valid for user: " + username);
            } else {
                System.out.println("[JWT Filter] Invalid token for user: " + username);
            }
        }

        // Cho phép request tiếp tục
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/")
                || path.startsWith("/auth/")
                || path.startsWith("/api/users")
                || path.startsWith("/users")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/error")
                || path.equals("/");
    }
}
