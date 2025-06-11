package com.Trekkit_Java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.ModifyDAO;

@Service
public class ModifyService {
	
	@Autowired private ModifyDAO md;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	@Transactional
	public boolean updateUserData(Long userId, String pw, String nickname, String profileUrl) {
		
		try {
			
			String hashedPw = null; // 암호화한 비밀번호를 저장하는 용도
			int result = 0; // 결과 확인
			
			if(pw != null && !pw.isEmpty()) {
				hashedPw = passwordEncoder.encode(pw);
			} 
			
			if(hashedPw != null) {
				// 만약 성공하면 return true 아니면 false
				result = md.updateData(userId, hashedPw, nickname, profileUrl);
			} else {
				result = md.updateDataNP(userId,nickname,profileUrl);
			}

	        return result > 0; // 업데이트된 행이 1개 이상이면 성공
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
		
	}

}
