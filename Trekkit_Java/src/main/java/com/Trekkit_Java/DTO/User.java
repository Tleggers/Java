package com.Trekkit_Java.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
	
	private long id; // 인덱스
	private String userid; // 아이디
	private String password; // 비밀번호
	private String nickname; // 닉네임
	private String email; // 이메일
	private LocalDateTime created; // 생성 시각
	private LocalDateTime updated; // 수정 시각
    private LocalDateTime recentLogin; // 최근 로그인 시각

}
