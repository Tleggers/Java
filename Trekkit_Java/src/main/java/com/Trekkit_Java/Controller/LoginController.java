package com.Trekkit_Java.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Service.LoginService;

// 2025-06-05 해야하는 일
// 1. 소셜 로그인 구현

@RestController
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
@RequestMapping("/login")
public class LoginController {
	
	@Autowired private LoginService ls;
	
	@PostMapping("/dologin")
    public ResponseEntity<?> doLogin(@RequestBody Map<String, String> req) {

        try {
            String userid = req.get("userid").trim();
            String password = req.get("password").trim();

            // ✅ 정규식 검사
            if (!userid.matches("^[a-zA-Z0-9]{1,16}$")
                    || password.length() > 16
                    || password.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣].*")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("형식 오류: 아이디 또는 비밀번호 형식이 올바르지 않습니다.");
            }

            // ✅ 로그인 시도 → 성공 시 JWT 토큰 반환
            Map<String, Object> result = ls.doLogin(userid, password);

            if (result != null) {
            	return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

}
