package com.Trekkit_Java.Config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Trekkit_Java.DAO.LoginDAO;
import com.Trekkit_Java.DTO.User;
import com.Trekkit_Java.Util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 2025-06-05 완

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
	@Autowired private JwtUtil jwtUtil;
	@Autowired private LoginDAO ld;
	
	// filter를 안거칠 url
	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        boolean shouldSkip = path.startsWith("/login/oauth2") ||
                path.startsWith("/oauth2/authorization") ||
                path.startsWith("/oauth2/callback") ||
                path.startsWith("/login/oauth2/code");

				return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
                                    throws ServletException, IOException {
    	
    	String token = null;
        String authHeader = request.getHeader("Authorization");
        String clientType = request.getHeader("X-Client-Type");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null && request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 이미 인증되어 있는 경우는 무시
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (token != null && clientType != null && jwtUtil.validateToken(token, clientType)) {
                try {
                    Long id = jwtUtil.extractUserId(token);
                    User user = ld.findById(id);
                    if (user != null) {
                        UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                user, null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (Exception ignored) {}
            }
        }
        
        chain.doFilter(request, response);
    }
}