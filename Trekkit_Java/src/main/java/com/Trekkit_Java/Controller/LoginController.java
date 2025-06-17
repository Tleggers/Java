package com.Trekkit_Java.Controller;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DAO.LoginDAO;
import com.Trekkit_Java.DTO.User;
import com.Trekkit_Java.Service.LoginService;
import com.Trekkit_Java.Util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

// 2025-06-05 해야하는 일
// 1. 소셜 로그인 구현

@RestController
@RequestMapping("/login")
public class LoginController {
	
	@Autowired private LoginService ls;
	@Autowired private JwtUtil jwtUtil;
	@Autowired private LoginDAO ld;
	
	@PostMapping("/dologin")
	public ResponseEntity<?> doLogin(@RequestBody Map<String, String> req,
	                                 @RequestHeader(value = "X-Client-Type", required = false) String clientType) {
	    try {
	    	
	        String userid = req.get("userid").trim();
	        String password = req.get("password").trim();

	        if (!userid.matches("^[a-zA-Z0-9]{1,16}$")
	                || password.length() > 16
	                || password.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣].*")) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("형식 오류: 아이디 또는 비밀번호 형식이 올바르지 않습니다.");
	        }

	        // 로그인 시도
	        Map<String, Object> result = ls.doLogin(userid, password, clientType);

	        if (result != null) {
	        	
	            String token = (String) result.get("token");

	            // 만약 clientType이 web이면 아래 실행
	            if ("web".equalsIgnoreCase(clientType)) {
	            	
	                // 쿠키로 내려줌
	                ResponseCookie cookie = ResponseCookie.from("jwt", token)
	                        .httpOnly(true)
	                        .secure(false) // secure은 https에서만 사용
	                        .sameSite("Lax")
	                        .maxAge(Duration.ofHours(4))
//	                        .maxAge(Duration.ofMinutes(5)) // 테스트용
	                        .path("/")
	                        .build();

	                return ResponseEntity.ok()
	                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
	                        .body("1");
	            } else {
	                // 앱이면 JSON 그대로 응답
	                return ResponseEntity.ok(result);
	            }
	        } else {
	        	return ResponseEntity.ok("0");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
	    }
	    
	}
	
	@PostMapping("/sociallogin")
	public ResponseEntity<?> doKakaoLogin(@RequestBody Map<String, Object> req,
												@RequestHeader(value = "X-Client-Type", required = false) String clientType) {

	    try {
	        String nickname = (String) req.get("nickname");
	        String profile = (String) req.get("profile");
	        String authid = String.valueOf(req.get("userid")); // 회원 아이디
	        String type = (String) req.get("type");

	        if (authid == null || nickname == null || profile == null) {
	            return ResponseEntity.badRequest().body("필수 항목 누락");
	        }

	        Map<String, Object> result = ls.doKakaoLogin(authid ,nickname, profile,type,clientType);
	        
	        // 만약 클라이언트 요청이 웹이라면 쿠키로 전달
	        if(clientType.equals("web")) {
	        	
	        	String token = (String) result.get("token");

		        // 웹이면 쿠키로 반환
		        if ("web".equalsIgnoreCase(clientType)) {
		        	
		            ResponseCookie cookie = ResponseCookie.from("jwt", token)
		                    .httpOnly(true)
		                    .secure(false) // 배포 시 true로 바꿔야 함 (https 환경일 때만)
		                    .sameSite("Lax")
		                    .maxAge(Duration.ofHours(4))
//		                    .maxAge(Duration.ofMinutes(5)) // 테스트용
		                    .path("/")
		                    .build();

		            return ResponseEntity.ok()
		                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
		                    .body("1"); 
		        }
	        	
	        }

	        // app이면 result만 전달
	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("서버 오류 발생");
	    }
	}
	
	// 아래 메소드는 페이지 들어갈 때마다 이 api 호출 및 필요한 데이터만 리턴해서 사용하기
	@GetMapping("/checklogin")
	public ResponseEntity<?> checkLogin(@CookieValue(value = "jwt", required = false) String token,
	                                    @RequestHeader(value = "X-Client-Type", required = false) String clientType) {
		
		Map<String, Object> result = new HashMap<>();
		
	    try {
	   
	        if (token == null || clientType == null || !jwtUtil.validateToken(token, clientType)) {
	        	result.put("isLogin", false);
	            return ResponseEntity.ok(result);
	        }

	        // userId는 인덱스를 의미
	        Long userId = jwtUtil.extractUserId(token);
	        if (userId == null) {
	            result.put("isLogin", false);
	            return ResponseEntity.ok(result);
	        }

	        User user = ld.findById(userId);
	        if (user == null) {
	            result.put("isLogin", false);
	            return ResponseEntity.ok(result);
	        }

	        // 데이터 리턴하는 곳
	        result.put("id", user.getUserid());
	        result.put("nickname", user.getNickname());
	        result.put("profile", user.getProfile());
	        result.put("logintype", user.getLogintype());
	        
	        result.put("isLogin", true); // 현재 로그인 여부

	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	    	result.put("isLogin", false);
	        result.put("error", "서버 내부 오류");
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류");
	    }
	    
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		
	    // 쿠키 삭제용 빈 쿠키 생성 (Same path & name, maxAge 0)
	    ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
	            .httpOnly(true)
	            .secure(false) // 개발 중엔 false, 배포 시 true
	            .sameSite("Lax")
	            .path("/")
	            .maxAge(0) // 즉시 만료
	            .build();

	    return ResponseEntity
	            .ok()
	            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
	            .body("로그아웃 완료");
	    
	}
	
	@RestController
	@RequestMapping("/login/oauth2")
	public class OAuth2LoginController {

	    @Value("${wep.api.url}")
	    private String frontendUrl; // ex: http://localhost:3000

	    @Autowired
	    private LoginService ls;

	    @GetMapping("/success")
	    public void oauthSuccess(Authentication auth, HttpServletResponse response) throws IOException {
	    	
    	    if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
	    		 
    	         // 인증 실패 or 인증 객체 없음 => 리다이렉트 혹은 에러 처리
    	         response.sendRedirect(frontendUrl + "/login?error=authnull");
    	         return;
    	    }
	    	 
	        OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();

	        // 구글에서 받은 정보
	        String authid = oAuth2User.getAttribute("sub");
	        String nickname = oAuth2User.getAttribute("name");
	        String profile = oAuth2User.getAttribute("picture");

	        // 기존 Kakao 처리 구조 재활용 (type: GOOGLE)
	        Map<String, Object> result = ls.doKakaoLogin(authid, nickname, profile, "GOOGLE", "web");

	        // JWT 추출해서 쿠키로 설정
	        String token = (String) result.get("token");

	        ResponseCookie cookie = ResponseCookie.from("jwt", token)
	                .httpOnly(true)
	                .path("/")
	                .maxAge(Duration.ofHours(4))
//	                .maxAge(Duration.ofMinutes(5)) // 테스트용
	                .sameSite("Lax")
	                .build();

	        response.setHeader("Set-Cookie", cookie.toString());

	        // React 홈으로 이동
	        response.sendRedirect(frontendUrl + "/");
	    }
	    
	}

}
