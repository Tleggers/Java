package com.Trekkit_Java.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.FindDAO;

@Service
public class FindService {

    @Autowired private FindDAO fd;

    @Transactional(readOnly = true)
    public Map<String, Object> findIdByEmail(String email) {
    	
        try {
            // 1. logintype 먼저 조회
            String loginType = fd.findLoginTypeByEmail(email);
            if (loginType == null || loginType.isEmpty()) {
                return null;
            }

            // 2. 리턴용 Map 생성
            Map<String, Object> result = new HashMap<>();
            result.put("logintype", loginType);

            // 3. LOCAL이면 userid 조회
            if ("LOCAL".equalsIgnoreCase(loginType)) {
                String userid = fd.findUserIdByEmail(email);
                result.put("userid", userid != null ? userid : "");
            } else {
                result.put("userid", "");
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }

    @Transactional
    public boolean updatePassword(String userid, String newPassword) {
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12); // cost값(반복횟수)
    	String hashedPw = encoder.encode(newPassword); 
        return fd.updatePassword(userid, hashedPw) > 0;
    }

    @Transactional(readOnly = true)
	public String checkUser(String userid, String email) {
		
    	String returnstr = "";
    	int count = 0;
    	
    	try {
    		
    		count = fd.checkUser(userid, email);
    		
    		if(count == 1) {
    			returnstr = "1";
    		}
    		
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return returnstr;
	}
    
}
