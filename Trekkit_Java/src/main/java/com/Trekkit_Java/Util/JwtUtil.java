package com.Trekkit_Java.Util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.Trekkit_Java.Config.JwtProperties;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // 생성자에서 주입받아서 SecretKey 생성
    public JwtUtil(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken(Long id) {
        return Jwts.builder()
        		.claim("id", id) // payload
                .setIssuedAt(new Date()) // 발급시간
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14)) // 지속시간
                .signWith(secretKey) // 인증키
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey)
                .build().parseClaimsJws(token)
                .getBody().get("id", Long.class);
    }
}
