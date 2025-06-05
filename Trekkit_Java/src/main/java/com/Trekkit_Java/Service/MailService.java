package com.Trekkit_Java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.AuthCodeDao;

// 2025-06-04

@Service
public class MailService {
	
	@Autowired private AuthCodeDao acd; 
	@Autowired private JavaMailSender mailSender;

	// 팀명 정해지면 넣기
	@Transactional
	public void sendMail(String cleanEmail) {

		try {
			// 1. 기존에 인증번호를 지운다.(혹여나 다른 인증번호랑 헷갈릴 수 있기 때문에)
			
			acd.deleteEmail(cleanEmail);
			
			// 2. 인증 코드 생성
            String authCode = generateCode();
            
            // 3. 이메일 발송
            SimpleMailMessage message = new SimpleMailMessage();
            String mailText = ""
            	    + "안녕하세요.\n\n"
            	    + "회원가입을 진행해주셔서 감사합니다.\n"
            	    + "아래의 인증 코드를 입력하시면 이메일 인증이 완료됩니다.\n\n"
            	    + "----------------------------------\n"
            	    + "📌 인증 코드: " + authCode + "\n"
            	    + "----------------------------------\n\n"
            	    + "해당 인증 코드는 보안을 위해 5분간만 유효합니다.\n"
            	    + "만약 본인이 요청하지 않은 경우 이 이메일을 무시해 주세요.\n\n"
            	    + "감사합니다.\n"
            	    + "- [Trek Kit] 드림 -\n\n"
            	    + "==================================\n\n"
            	    + "Hello,\n\n"
            	    + "Thank you for signing up.\n"
            	    + "Please enter the verification code below to complete your email verification.\n\n"
            	    + "----------------------------------\n"
            	    + "📌 Verification Code: " + authCode + "\n"
            	    + "----------------------------------\n\n"
            	    + "This verification code is valid for 5 minutes for security reasons.\n"
            	    + "If you did not request this email, please disregard it.\n\n"
            	    + "Thank you.\n"
            	    + "- From [Trek Kit]\n\n"
            	    + "───────────────────────────────────────\n"
            	    + "ⓒ 2025 YourCompany. All rights reserved.\n"
            	    + "고객센터: support@yourcompany.com | 010-1234-5678\n"
            	    + "주소: 서울 서초구 서초동 1318-2  8층\n"
            	    + "───────────────────────────────────────";
            
            message.setTo(cleanEmail);
            message.setSubject("[이메일 인증] 인증 코드 발송");
            message.setText(mailText);
            mailSender.send(message);
            
            // 4. DB 저장
            acd.insertAuthCode(cleanEmail,authCode);
            
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// 인증 코드 만드는 알고리즘
	private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 숫자
    }

	@Transactional(readOnly = true)
	public boolean checkAuthCode(String cleanEmail, String authcode) {
		
		int count = 0; // 맞으면 1 아니면 0
		boolean re = false; // 리턴할거
		
		try {
			count = acd.verifyCode(cleanEmail,authcode);
			if(count > 0) {
				re = true;
			} else {
				re = false;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
	}

}
