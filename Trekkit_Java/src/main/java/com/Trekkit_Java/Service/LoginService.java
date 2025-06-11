package com.Trekkit_Java.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.LoginDAO;
import com.Trekkit_Java.DTO.User;
import com.Trekkit_Java.Util.JwtUtil;

// 2025-06-05 완

@Service
public class LoginService {
	
	@Autowired private LoginDAO ld;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private JwtUtil jwtUtil;
	@Value("${app.api.url}") private String apiUrl;

	// 일반 로그인
	@Transactional(readOnly = true)
	public Map<String, Object> doLogin(String cleanid, String cleanpw, String clientType) {
		
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
            String token = jwtUtil.generateToken(id,user.getUsertype(),clientType); // 여기서 payload에 id가 들어감
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("nickname", user.getNickname());
            result.put("logintype", user.getLogintype());
            result.put("index", user.getId());
            
            if(user.getProfile() == null) {
            	result.put("profile", ""); 
            } else {
            	result.put("profile", apiUrl + user.getProfile());
            }

            return result;
	        
		} catch(Exception e) {
			e.printStackTrace();
		}

        return null;
		
	}

     //	카카오 로그인
	@Transactional
	public Map<String, Object> doKakaoLogin(String authid,String nickname, String profile, String type, String clientType) {
		
		int check = 0; // 회원가입 성공 여부
		
	    try {
	        // ✅ DB에 유저 존재 여부 확인 (소셜 로그인은 이메일 기준)
	        Map<String, Object> who = ld.findUserByAuthId(authid, type);

	        if (who == null) {
	            // 없으면 회원가입 처리
	        	check = ld.insertKakaoUser(authid, nickname, profile,type);
	        	
	        	// 회원가입이 실패하면 return null;
	        	if(check != 1) {
	        		return null;
	        	}
	        }
	        
	        // 이메일로 유저 찾기
	        User user = ld.findIdByAuthid(authid,type);
	        
	        // ✅ JWT 발급
	        String token = jwtUtil.generateToken(user.getId(),user.getUsertype(),clientType);

	        // ✅ 닉네임, 프로필도 포함해서 리턴
	        Map<String, Object> result = new HashMap<>();
	        result.put("token", token);
	        result.put("nickname", user.getNickname());
	        result.put("logintype", user.getLogintype());
	        result.put("index", user.getId());

	        if (user.getProfile() == null || user.getProfile().isEmpty()) {
	            result.put("profile", ""); 
	        } else if (user.getProfile().startsWith("http")) {
	            result.put("profile", user.getProfile()); // 이미 URL이면 그대로 사용
	        } else {
	        	result.put("profile", apiUrl + user.getProfile());
	        }

	        return result;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    
	}

}
