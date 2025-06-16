package com.Trekkit_Java.Controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Trekkit_Java.DAO.LoginDAO;
import com.Trekkit_Java.DAO.ModifyDAO;
import com.Trekkit_Java.DTO.User;
import com.Trekkit_Java.Service.ModifyService;
import com.Trekkit_Java.Util.ExtractToken;
import com.Trekkit_Java.Util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/modify")
public class ModifyController {
	
	@Autowired private JwtUtil jwtUtil;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private LoginDAO ld;
	@Autowired private ModifyService ms;
	@Autowired private ModifyDAO md;
	
	@PostMapping("/checkuser")
	public ResponseEntity<Boolean> checkUser(@RequestBody Map<String, String> req, HttpServletRequest request) {
	    try {
	        // 토큰과 clientType 추출
	        String token = ExtractToken.extractToken(request);
	        String clientType = request.getHeader("X-Client-Type");

	        if (token == null || clientType == null || !jwtUtil.validateToken(token, clientType)) {
	            return ResponseEntity.status(401).body(false);
	        }

	        // userId 추출
	        Long userId = jwtUtil.extractUserId(token);
	        if (userId == null) {
	            return ResponseEntity.status(401).body(false);
	        }

	        // 사용자 정보 조회
	        User user = ld.findById(userId);
	        if (user == null) {
	            return ResponseEntity.status(404).body(false);
	        }

	        // 비밀번호 일치 확인
	        String inputPw = req.get("pw");
	        boolean isMatch = passwordEncoder.matches(inputPw, user.getPassword());

	        return ResponseEntity.ok(isMatch);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body(false);
	    }
	    
	}
	
	@PostMapping("/getUserData")
	public ResponseEntity<?> getUserData(HttpServletRequest request) {
		
	    try {
	    	
	    	// 1. 토큰 및 clientType 추출
	        String token = ExtractToken.extractToken(request);
	        String clientType = request.getHeader("X-Client-Type");

	        if (token == null || clientType == null || !jwtUtil.validateToken(token, clientType)) {
	            return ResponseEntity.status(401).body("인증 실패");
	        }

	        // 2. 토큰에서 유저 ID 추출
	        Long userId = jwtUtil.extractUserId(token);
	        if (userId == null) {
	            return ResponseEntity.status(401).body("토큰에서 사용자 정보를 추출할 수 없습니다");
	        }

	        // 3. DB 조회
	        User user = ld.findById(userId);
	        if (user == null) {
	            return ResponseEntity.status(404).body("사용자 없음");
	        }

	        // 4. 필요한 데이터만 가공해서 리턴
	        Map<String, Object> result = new HashMap<>();
	        result.put("id", user.getUserid()); 
	        result.put("authid", user.getOauthid());
	        result.put("nickname", user.getNickname());
	        result.put("profile", user.getProfile());
	        result.put("logintype", user.getLogintype());

	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("서버 내부 오류");
	    }
	    
	}
	
	@PostMapping("/updateUserData")
	public ResponseEntity<?> updateUserData(
	    @RequestParam(value = "pw", required = false) String pw,
	    @RequestParam("nickname") String nickname,
	    @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
	    HttpServletRequest request
	) {
		
		try {
			
			// 1. 토큰 및 clientType 추출
	        String token = ExtractToken.extractToken(request);
	        String clientType = request.getHeader("X-Client-Type");

	        if (token == null || clientType == null || !jwtUtil.validateToken(token, clientType)) {
	            return ResponseEntity.status(401).body(Map.of("error", "인증 실패"));
	        }

	        // 2. 유저 ID 추출
	        Long userId = jwtUtil.extractUserId(token);
	        if (userId == null) {
	            return ResponseEntity.status(401).body(Map.of("error", "토큰에서 유저 정보를 추출할 수 없음"));
	        }

	        // 3. 닉네임 검증
	        if (nickname == null || !nickname.matches("^[a-zA-Z0-9가-힣]{1,16}$")) {
	            return ResponseEntity.badRequest().body(Map.of("error", "닉네임은 한글/영문/숫자만 포함하며 1~16자 이내여야 합니다."));
	        }

	        // 4. 프로필 이미지 저장
	        String profileUrl = null;
	        if (profileImage != null && !profileImage.isEmpty()) {
	            String saveDir = System.getProperty("user.dir") + "/uploads/profile/";
	            String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
	            String fullPath = saveDir + fileName;

	            File dir = new File(saveDir);
	            if (!dir.exists()) dir.mkdirs();

	            profileImage.transferTo(new File(fullPath));
	            profileUrl = "/profile/" + fileName;
	        }

	        // 5. 서비스 호출
	        boolean updated = ms.updateUserData(userId, pw, nickname, profileUrl);
	        if (!updated) {
	            return ResponseEntity.status(500).body(Map.of("error", "업데이트 실패"));
	        }

	        // 6. JSON 응답
	        return ResponseEntity.ok(Map.of(
	            "message", "정보 수정 완료",
	            "profile", profileUrl
	        ));

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body(Map.of("error", "서버 내부 오류"));
	    }
		
	}
	
	@PostMapping("/delete")
	public ResponseEntity<?> deleteUser(HttpServletRequest request) {
		
	    try {
	    	
	        // 1. 헤더에서 토큰 및 clientType 추출
	        String authHeader = request.getHeader("Authorization");
	        String clientType = request.getHeader("X-Client-Type");

	        if (authHeader == null || !authHeader.startsWith("Bearer ") || clientType == null) {
	            return ResponseEntity.status(401).body("인증 헤더 누락");
	        }

	        String token = authHeader.substring(7); // "Bearer " 제거

	        // 2. 토큰 유효성 검증
	        if (!jwtUtil.validateToken(token, clientType)) {
	            return ResponseEntity.status(401).body("유효하지 않은 토큰");
	        }

	        // 3. 토큰에서 유저 ID 추출
	        Long userId = jwtUtil.extractUserId(token);
	        if (userId == null) {
	            return ResponseEntity.status(401).body("유저 ID 추출 실패");
	        }

	        // 4. 사용자 존재 확인
	        User user = ld.findById(userId);
	        if (user == null) {
	            return ResponseEntity.status(404).body("사용자 없음");
	        }

	        // 5. 사용자 삭제
	        int result = md.deleteUser(userId);
	        if (result == 0) {
	            return ResponseEntity.status(500).body("삭제 실패");
	        }

	        return ResponseEntity.ok("회원 탈퇴 완료");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("서버 오류");
	    }
	    
	}

}
