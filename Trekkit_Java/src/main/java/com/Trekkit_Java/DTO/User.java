package com.Trekkit_Java.DTO;

import java.time.LocalDateTime;

import lombok.Data;

// 2025-06-02 ~ 2025-06-05

@Data
public class User {
	
	private long id; // 인덱스
	private String userid; // 아이디
	private String password; // 비밀번호
	private String nickname; // 닉네임
	private String email; // 이메일
	private String profile; // 프로필 사진 url
	private String logintype; // 어떻게 로그인 햇는지(ex.카카오,local,구글)
	private String oauthid; // 소셜 로그인 고유 id
	private LocalDateTime created; // 생성 시각
	private LocalDateTime updated; // 수정 시각
    private LocalDateTime recentLogin; // 최근 로그인 시각

}
