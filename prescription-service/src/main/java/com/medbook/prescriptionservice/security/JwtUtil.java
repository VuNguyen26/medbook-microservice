package com.medbook.prescriptionservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Tiện ích xử lý JWT cho prescription-service
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Tạo khóa ký HMAC-SHA256 từ secretKey
     */
    private Key getSigningKey() {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        log.debug("🔑 Using signing key: {}", secretKey);
        return key;
    }

    /**
     * Giải mã toàn bộ claims từ token
     */
    public Claims extractAllClaims(String token) throws JwtException {
        log.debug("🔍 Extracting claims from token: {}", token.substring(0, Math.min(token.length(), 30)) + "...");
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Lấy username (subject) từ token
     */
    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        log.info("👤 Extracted username: {}", username);
        return username;
    }

    /**
     * Lấy role (quyền) từ token
     */
    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        String result = role != null ? role.toString() : null;
        log.info("🎭 Extracted role: {}", result);
        return result;
    }

    /**
     * Kiểm tra token có còn hạn hay không
     */
    public boolean isTokenValid(String token) {
        Date expiration = null;
        boolean valid = false;

        try {
            Claims claims = extractAllClaims(token);
            expiration = claims.getExpiration();
            valid = expiration != null && expiration.after(new Date());

            log.info("🕒 Token expiration: {}", expiration);
            log.info("✅ Token valid: {}", valid);

            return valid;
        } catch (ExpiredJwtException e) {
            log.warn("⚠️ Token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("❌ Malformed JWT: {}", e.getMessage(), e);
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("❌ Unsupported JWT: {}", e.getMessage(), e);
            return false;
        } catch (SignatureException e) {
            log.error("❌ Invalid JWT signature (secret key mismatch): {}", e.getMessage(), e);
            return false;
        } catch (JwtException e) {
            log.error("❌ General JWT error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("💥 Unexpected error while validating JWT: {}", e.getMessage(), e);
            return false;
        }
    }
}
