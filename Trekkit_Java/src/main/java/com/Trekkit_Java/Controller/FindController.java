package com.Trekkit_Java.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Service.FindService;
import com.Trekkit_Java.Util.Validate;

@RestController
@RequestMapping("/find")
public class FindController {
	
	@Autowired private FindService fs;

	@PostMapping("/findid")
    public ResponseEntity<Map<String, Object>> findId(@RequestBody Map<String, String> req) {
		
        try {
            String email = req.get("email").trim();
            
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // 이메일 정규식

            if (email == null || email.trim().isEmpty() 
               || !email.matches(emailRegex) || email.matches(".*[^a-zA-Z0-9@._%+-].*")) {
                return ResponseEntity.badRequest().build();
            }

            // 서비스 호출
            Map<String, Object> result = fs.findIdByEmail(email.trim());

            if (result == null || result.isEmpty()) {
                // 아이디나 로그인 타입을 찾지 못한 경우
                return ResponseEntity.ok(Map.of("userid", "", "logintype", ""));
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        
    }
	
	@PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
		
		try {
			
			String userid = req.get("userid");
	        String newPassword = req.get("newPassword");
	        
	        // 유효성 검사: userid는 영어/숫자만 허용
	        if (userid == null || !userid.matches("^[a-zA-Z0-9]+$")) {
	            return ResponseEntity.badRequest().body("아이디 형식이 올바르지 않습니다.");
	        }

	        // 유효성 검사: 비밀번호 16자 이하
	        if (newPassword == null || newPassword.length() > 16) {
	            return ResponseEntity.badRequest().body("비밀번호는 16자 이하로 입력해야 합니다.");
	        }

	        boolean updated = fs.updatePassword(userid, newPassword);
	        
	        if (updated) {
	            return ResponseEntity.ok().build();
	        } else {
	            return ResponseEntity.status(404).body("User not found or update failed");
	        }
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
        
    }
	
	@PostMapping("/checkAuth")
	public String checkUser(@RequestBody Map<String, String> req) {
		
		String returnstr = "";
		Validate val = new Validate();
		
		try {
			
			String userid = req.get("userid").trim();
			String email = req.get("email").trim();
			
			boolean useridVal = val.useridValidate(userid);
			boolean emailVal = val.emailValidate(email);
			
			if(useridVal && emailVal) {
				returnstr = fs.checkUser(userid,email);
				if(returnstr.equals("1")) {
					returnstr = "";
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	
		return returnstr;
		
	}

}
