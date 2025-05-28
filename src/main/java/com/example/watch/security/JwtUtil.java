package com.example.watch.security;

import com.example.watch.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret"; // 32 ký tự
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(User user) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + 86400000; // 1 ngày, bạn có thể tuỳ chỉnh
        Date exp = new Date(expMillis);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .expiration(exp) // Thêm dòng này!
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key) // <<== Dùng setSigningKey cho 0.12.x (nếu gặp lỗi verifyWith)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}