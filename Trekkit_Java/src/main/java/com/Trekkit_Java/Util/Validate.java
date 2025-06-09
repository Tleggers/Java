package com.Trekkit_Java.Util;

//2025-06-03

public class Validate {
	
	// 회원가입 데이터 정규식 처리
	public boolean dateValidate(String userid, String password, String email, String nickname) {

		// 아이디: 영문자/숫자 1~16자리
		if (userid == null || !userid.matches("^[a-zA-Z0-9]{1,16}$") || userid.equals("admin")) return false; 
		
		// 비밀번호: 영문자/숫자/특수문자 1~16자리
		if (password == null || !password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{1,16}$") || password.equals("admin")) return false;
		
		// 이메일: 형식 체크
		if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) return false;
		
		// 특수기호만 들어 있으면 false
		if (nickname.matches("^[^a-zA-Z0-9가-힣\\s]+$")) return false;
		
		return true; // 모든 조건 통과
		
	}

}
