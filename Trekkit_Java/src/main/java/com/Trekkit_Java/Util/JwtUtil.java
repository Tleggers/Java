package com.Trekkit_Java.Util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.Trekkit_Java.Config.JwtProperties;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

//2025-06-05

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // 생성자에서 주입받아서 SecretKey 생성
    public JwtUtil(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    // 토큰 생성
    public String generateToken(Long id,String type, String clientType) {
        return Jwts.builder()
        		.claim("id", id) // payload
        		.claim("role", type)
        		.setIssuer("trekkit") // 발급자
        		.setAudience(clientType) // 프론트 타입
                .setIssuedAt(new Date()) // 발급시간
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 4)) // 지속시간 하루
//                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 5))  // 테스트용
                .signWith(secretKey) // 인증키
                .compact();
    }

    // 토큰 인증
    public boolean validateToken(String token, String clientType) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer("trekkit")
                .requireAudience(clientType) 
                .build()
                .parseClaimsJws(token);
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
