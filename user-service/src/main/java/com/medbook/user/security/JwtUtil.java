package com.medbook.user.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Khóa bí mật (dài > 32 ký tự)
    private static final String SECRET_KEY = "thisIsASecretKeyForJWTMedBookProject12345";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1 ngày (ms)

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ===================== TẠO TOKEN =====================
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return buildToken(claims, email);
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===================== TRÍCH XUẤT DỮ LIỆU =====================
    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            Object role = extractAllClaims(token).get("role");
            return role != null ? role.toString() : null;
        } catch (JwtException e) {
            return null;
        }
    }

    // ===================== KIỂM TRA TOKEN =====================
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username != null
                && username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // ===================== NỘI BỘ =====================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
