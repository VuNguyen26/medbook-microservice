package com.medbook.patientservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        // HMAC-SHA256 yêu cầu key >= 32 bytes
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Giải mã toàn bộ Claims từ token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Lấy role từ token
    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    // Kiểm tra token còn hạn hay không
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }
}
