package com.Trekkit_Java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.SignupDao;

//2025-06-03

@Service 
public class SignupService {
	
	@Autowired private SignupDao sd;  

	@Transactional(readOnly = true) // readOnly를 함으로써 불필요한 행위를 안함으로 인해 성능 향상 가능
	public boolean checkId(String userid) {
		
		int count = 0; // 아이디가 몇개 존재하는지 저장할 변수
		boolean re = false; // Controller로 리턴할 변수
		
		try {
			
			count = sd.checkDupId(userid); // 중복 확인
			
			if(count > 0) {
				re = false;
			} else {
				re = true;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
			
		return re;
	}

	@Transactional(readOnly = true)
	public boolean checkE(String cleanEmail) {
		
		int count = 0; // 아이디가 몇개 존재하는지 저장할 변수
		boolean re = false; // Controller로 리턴할 변수
		
		try {
			
			count = sd.checkDupE(cleanEmail);
			
			if(count > 0) {
				re = false;
			} else {
				re = true;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return re;
	}

	@Transactional(readOnly = true)
	public boolean checkDupNick(String nickname) {
		
		int count = 0; // 아이디가 몇개 존재하는지 저장할 변수
		boolean re = false; // Controller로 리턴할 변수
		
		try {
			
			count = sd.checkDupNickName(nickname);
			
			if(count > 0) {
				re = false;
			} else {
				re = true;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		return re;
	}

	@Transactional
	public boolean dosignup(String cuserid, String cpassword, String cemail, String cnickname, String imageUrl) {
		
		boolean returnstr = false; // Controller로 리턴할 변수
		int fuck = 0; // 성공 횟수
	    
	    try {
	    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // cost값(반복횟수)
	    	String hashedPw = encoder.encode(cpassword); // 암호화한 비밀번호를 hashedPw에 저장 	
	    	
	    	fuck = sd.doSignup(cuserid,hashedPw,cemail,cnickname,imageUrl);
	    	
	    	if(fuck == 1) {
	    		returnstr = true;
	    	} else {
	    		returnstr = false;
	    	}
	    	
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
		return returnstr;
	}

	@Transactional(readOnly = true)
	public boolean checkUserByIdAndEmail(String cleanUserid, String cleanEmail) {
		
		try {
			return sd.existsByIdAndEmail(cleanUserid, cleanEmail) > 0;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
