package com.Trekkit_Java.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Service.MailService;
import com.Trekkit_Java.Service.SignupService;
import com.Trekkit_Java.Util.FormatPhoneNumber;
import com.Trekkit_Java.Util.Validate;

@RestController
@CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
@RequestMapping("/signup")
public class SignupController {
	
	@Autowired private SignupService ss;
	@Autowired private MailService ms;
	
	@PostMapping("/dosignup")
	public String doSignup(@RequestBody Map<String, String> req) {
		
		// 정규식 함수가 들어있는 객체
		Validate val = new Validate(); 
		
		String returnstr = "";
		
		try {
			
			String userid = req.get("id"); 
			String password = req.get("pw");
			String email = req.get("email");
			String name = req.get("username");
			String nickname = req.get("nickname");
			String birthStr = req.get("birth");
			String cbirthStr = birthStr.trim();
			String phonenumber = req.get("mobile");
			String gender = req.get("gender");
			LocalDate birth = null;
		    if (birthStr != null && !birthStr.isEmpty() && birthStr.matches("^[0-9\\-]+$")) {
		    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		    	birth = LocalDate.parse(cbirthStr, formatter);
		    }
		    
		    // 공백 제거 후 정규식 비교 시작
		    String cuserid = userid.trim();
		    String cpassword = password.trim();
		    String cemail = email.trim();
		    String cname = name.trim();
		    String cnickname = nickname.trim();
		    String cnumber = phonenumber.trim();
		    
		    // 받아온 데이터 정규식 비교하며 백엔드에서 처리
		    boolean isValid = val.dateValidate(cuserid, cpassword, cemail, cname, cnickname, birth, cnumber, gender);
		    
		    // 정규식 처리가 false면(즉 쓸데 없는게 들어가있으면) 리턴 0 아니면 리턴 1
		    if(isValid == false) {
		    	returnstr = "0";
		    } else {
		    	returnstr = ss.dosignup(cuserid,cpassword,cemail,cname,cnickname,birth,cnumber,gender);
		    }
		    
		} catch(Exception e) {
			System.out.println(e);
		}
		
		return returnstr;
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
	
	@PostMapping("/checkAuthCode")
	public boolean checkAuthCode(@RequestBody Map<String, String> req) {
		
		boolean re = false;
		
		try {
			String email = req.get("email");
			String cleanEmail = email.trim();
			String authcode = req.get("authCode");
			
			re = ms.checkAuthCode(cleanEmail, authcode);
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
			String cleanNickName = nickname.trim();
			
			if(cleanNickName == null || cleanNickName.equals("") || !cleanNickName.matches("^[가-힣a-zA-Z0-9]+$")) {
				re = false;
			} else {
				re = ss.checkDupNick(cleanNickName);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
	}
	
	@PostMapping("/checkDupMobile")
	public boolean checkDupMobile(@RequestBody Map<String, String> req) {
		
		boolean re = false;
		
		try {
			String mobile = req.get("mobile");
			String cleanMobile = mobile.trim();
			
			if(cleanMobile == null || cleanMobile.equals("") || cleanMobile.matches(".*[^0-9].*")) {
				re = false;
			} else {
				String enmobile = FormatPhoneNumber.formatPhoneNumber(cleanMobile);
				re = ss.checkDupMobile(enmobile);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
	}

}
