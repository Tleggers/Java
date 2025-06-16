package com.Trekkit_Java.Util;

import jakarta.servlet.http.HttpServletRequest;

public class ExtractToken {
	
	// JWT 추출 함수
	public static String extractToken(HttpServletRequest request) {
	    String authHeader = request.getHeader("Authorization");

	    // 1. Authorization 헤더에서 Bearer 토큰 우선 추출
	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        return authHeader.substring(7);
	    }

	    // 2. 웹의 경우 쿠키에서 추출 (쿠키 이름: jwt)
	    if (request.getCookies() != null) {
	        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
	            if ("jwt".equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }

	    return null;
	}

}
