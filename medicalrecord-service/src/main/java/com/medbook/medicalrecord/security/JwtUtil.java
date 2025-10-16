package com.medbook.medicalrecord.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    // Tạo khóa ký HMAC-SHA256 (đảm bảo UTF-8 để tránh lỗi encoding)
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Giải mã toàn bộ claims từ token
    public Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Lấy username (subject) từ token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Lấy role (quyền) từ token
    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    // Kiểm tra token có còn hạn không
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
            return false;
        } catch (JwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }
}
