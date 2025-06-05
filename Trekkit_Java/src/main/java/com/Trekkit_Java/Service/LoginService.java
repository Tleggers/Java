package com.Trekkit_Java.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.LoginDAO;
import com.Trekkit_Java.DTO.User;
import com.Trekkit_Java.Util.JwtUtil;

@Service
public class LoginService {
	
	@Autowired private LoginDAO ld;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private JwtUtil jwtUtil;

	// 일반 로그인
	@Transactional(readOnly = true)
	public Map<String, Object> doLogin(String cleanid, String cleanpw) {
		
		try {
			
			// 유저 id 조회
			Long id = ld.findByUserid(cleanid);
			if (id == null) return null;  // 아이디 없으면 return null;
			
			// 유저 정보 조회
            User user = ld.findById(id);
            if (user == null) return null; // 유저 없음
			
            // 비밀번호 일치 여부 확인
            if (!passwordEncoder.matches(cleanpw, user.getPassword())) return null;

            // 로그인 성공 시
            String token = jwtUtil.generateToken(id); // 여기서 payload에 id가 들어감
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("nickname", user.getNickname());
            
            if(user.getProfile() == null) {
            	result.put("profile", ""); 
            } else {
            	result.put("profile", "http://10.0.2.2:30000" + user.getProfile()); // static 경로 포함
            }

            return result;
	        
		} catch(Exception e) {
			e.printStackTrace();
		}

        return null;
		
	}

}
