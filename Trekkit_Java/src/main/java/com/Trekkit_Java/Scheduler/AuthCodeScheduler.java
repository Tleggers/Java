package com.Trekkit_Java.Scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.Trekkit_Java.DAO.AuthCodeDao;

@Component
@EnableScheduling
public class AuthCodeScheduler {
	
	@Autowired private AuthCodeDao acd;
	
	@Scheduled(fixedRate = 60000) // 1분마다 실행
    public void deleteExpiredAuthCodes() {
        acd.deleteExpiredCodes(); // 1분만에 3분이 지난 인증번호를 자동으로 삭제
    }

}
