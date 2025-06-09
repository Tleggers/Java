package com.Trekkit_Java.Controller;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Trekkit_Java.Service.MailService;
import com.Trekkit_Java.Service.SignupService;
import com.Trekkit_Java.Util.Validate;

// 2025-06-04 완

@RestController
@CrossOrigin(origins = {
					        "http://localhost:3000",           // 로컬 테스트용
					        "http://192.168.0.7:3000",      // 실기기 (같은 와이파이 IP)
					        "http://192.168.0.51:3000"     // 실기기2
							}, 
				allowCredentials="true")
@RequestMapping("/signup")
public class SignupController {
	
	@Autowired private SignupService ss;
	@Autowired private MailService ms;
	
	@PostMapping("/dosignup")
	public ResponseEntity<Boolean> doSignup(
	    @RequestParam("id") String id,
	    @RequestParam("pw") String pw,
	    @RequestParam("email") String email,
	    @RequestParam("nickname") String nickname,
	    @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
	) {
	    Validate val = new Validate();
	    
	    try {
	        String cuserid = id.trim();
	        String cpassword = pw.trim();
	        String cemail = email.trim();
	        String cnickname = nickname.trim();
	        
	        boolean isValid = val.dateValidate(cuserid, cpassword, cemail, cnickname);
	        
	        if (!isValid) {
	            return ResponseEntity.ok(false); // ❌ 정규식 실패
	        }
	        
	        if(cnickname == null || cnickname.equals("")) {
	        	cnickname = cuserid;
	        }
	        
	        String imageUrl = null;
	        if (profileImage != null && !profileImage.isEmpty()) {

	        	// 파일 저장 경로
	            String saveDir = System.getProperty("user.dir") + "/uploads/profile/";
	            
	            // 파일 이름 + 풀경로
	            String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
	            String fullPath = saveDir + fileName;
	            
	            // 폴더 없으면 생성
	            File dir = new File(saveDir);
	            if (!dir.exists()) dir.mkdirs();

	            // 실제 파일 저장
	            profileImage.transferTo(new File(fullPath));
	            
	            // DB에 저장할 경로
	            imageUrl = "/profile/" + fileName;
	            
	        }

	        // 회원가입 처리
	        boolean success = ss.dosignup(cuserid, cpassword, cemail, cnickname, imageUrl);

	        return ResponseEntity.ok(success); // ✅ true 또는 false 응답

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body(false); // ❌ 서버 에러 시 false
	    }
	}
	
	@PostMapping("/checkDupid")
	public boolean checkDupid(@RequestBody Map<String, String> req) {
		
		boolean re = false;
		
		try {
			String userid = req.get("id");
			String cleanUserid = userid.trim();
			
			// 전달값(id)가 null or 빈문자열 or 특수문자 포함시 false 아니면 service파일로 전달
			if(cleanUserid == null || cleanUserid.equals("") || cleanUserid.matches(".*[^a-zA-Z0-9].*")) {
				re = false;
			} else {
				re = ss.checkId(cleanUserid);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
		
	}
	
	@PostMapping("/checkDupEmail")
	public boolean checkDupE(@RequestBody Map<String, String> req) {
		
		boolean re = false; // return할 변수
		
		try {
			String email = req.get("email");
			String cleanEmail = email.trim();
			
			if(cleanEmail == null || cleanEmail.equals("") || cleanEmail.matches(".*[^a-zA-Z0-9@._%+-].*")) {
				re = false;
			} else {
				re = ss.checkE(cleanEmail);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
	}
	
	@PostMapping("/sendMail") 
	public void sendMail(@RequestBody Map<String, String> req) {
		
		try {
			
			String email = req.get("email");
			String cleanEmail = email.trim();
			
			if(cleanEmail == null || cleanEmail.equals("") || cleanEmail.matches(".*[^a-zA-Z0-9@._%+-].*")) {}
			else {
				ms.sendMail(cleanEmail);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@PostMapping("/sendFindMail") 
	public void sendFindMail(@RequestBody Map<String, String> req) {
		
		try {
			
			String email = req.get("email");
			String cleanEmail = email.trim();
			
			if(cleanEmail == null || cleanEmail.equals("") || cleanEmail.matches(".*[^a-zA-Z0-9@._%+-].*")) {}
			else {
				ms.sendFindMail(cleanEmail);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@PostMapping("/sendFindPwMail")
	public ResponseEntity<String> sendFindPwMail(@RequestBody Map<String, String> req) {
		
	    try {
	        String email = req.get("email");
	        String userid = req.get("userid");

	        if (email == null || userid == null) {
	            return ResponseEntity.badRequest().body("입력값이 부족합니다.");
	        }

	        String cleanEmail = email.trim();
	        String cleanUserid = userid.trim();

	        // 형식 검증
	        if (cleanEmail.equals("") || cleanUserid.equals("") ||
	            cleanEmail.matches(".*[^a-zA-Z0-9@._%+-].*") ||
	            cleanUserid.matches(".*[^a-zA-Z0-9].*")) {
	            return ResponseEntity.badRequest().body("형식이 올바르지 않습니다.");
	        }

	        // 유저 존재 여부 확인
	        boolean userExists = ss.checkUserByIdAndEmail(cleanUserid, cleanEmail);
	        if (!userExists) {
	            return ResponseEntity.status(404).body("해당 유저를 찾을 수 없습니다.");
	        }

	        // 메일 전송
	        ms.sendFindMail(cleanEmail);
	        return ResponseEntity.ok("메일 전송 완료");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("서버 오류");
	    }
	    
	}
	
	@PostMapping("/checkAuthCode")
	public boolean checkAuthCode(@RequestBody Map<String, String> req) {
		
		boolean re = false;
		
		try {
			String email = req.get("email");
			String cleanEmail = email.trim();
			String authcode = req.get("authCode");
			
			if(authcode == null || authcode.equals("") || !authcode.matches("[0-9]+")) {
				return false;
			} else {
				re = ms.checkAuthCode(cleanEmail, authcode);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
		
	}
	
	@PostMapping("/checkDupNickname")
	public boolean checkDupNickname(@RequestBody Map<String, String> req) {
		
		boolean re = false; 
		
		try {
			String nickname = req.get("nickname");
			String cleanNickName = nickname == null ? "" : nickname.trim();

			if (cleanNickName.equals("")) {
			    // 입력 안 했으면 유효한 값으로 간주 (닉네임은 선택사항)
			    re = true;
			} else if (!cleanNickName.matches("^[가-힣a-zA-Z0-9]+$")) {
			    // 특수문자 등 포함되면 거절
			    re = false;
			} else {
			    // 중복체크
			    re = ss.checkDupNick(cleanNickName);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
	}

}
