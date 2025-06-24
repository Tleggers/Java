package com.Trekkit_Java.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Service.PayService;
import com.Trekkit_Java.Util.ExtractToken;
import com.Trekkit_Java.Util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pay")
public class PayController {
	
	@Autowired private PayService ps;
	@Autowired private JwtUtil jwtUtil;
	
	@PostMapping("/add")
    public ResponseEntity<?> addPoint(@RequestBody Map<String, Object> req,
                                      HttpServletRequest request) {
		
		try {
			
            // 1. 토큰 및 clientType 추출
            String token = ExtractToken.extractToken(request);
            String clientType = request.getHeader("X-Client-Type");

            if (token == null || clientType == null || !jwtUtil.validateToken(token, clientType)) {
                return ResponseEntity.status(401).body(Map.of("error", "인증 실패"));
            }

            // 2. 토큰에서 유저 ID 추출
            Long tokenUserId = jwtUtil.extractUserId(token);
            if (tokenUserId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "토큰에서 사용자 정보를 추출할 수 없음"));
            }

            // 3. 요청 body에서 유저 ID 추출 (프론트에서 보낸 값)
            Long userid = ((Number) req.get("id")).longValue(); // 프론트에서 보내준 userid, 무조건 숫자라는게 보장 될 때만 사용
            if (userid == null || !tokenUserId.equals(userid)) {
                return ResponseEntity.status(403).body(Map.of("error", "유저 인증 정보 불일치"));
            }

            // 4. 포인트 추출
            int point = ((Number) req.get("point")).intValue();
            if (point <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "포인트 값이 유효하지 않음"));
            }

            // 5. 서비스 호출
            int result = ps.addPoint(userid, point);

            if (result == 1) {
                return ResponseEntity.ok(Map.of("result", "포인트 지급 성공"));
            } else {
                return ResponseEntity.status(400).body(Map.of("error", "포인트 지급 실패"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "서버 내부 오류"));
        }
		
	}

}
