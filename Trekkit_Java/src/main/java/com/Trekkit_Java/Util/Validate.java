package com.Trekkit_Java.Util;

import java.time.LocalDate;

public class Validate {
	
	// 회원가입 데이터 정규식 처리
	public boolean dateValidate(String userid, String password, String email,
            String name, String nickname, LocalDate birth,
            String phonenumber, String gender) {

		// 아이디: 영문자/숫자 4~16자리
		if (userid == null || !userid.matches("^[a-zA-Z0-9]{1,16}$")) return false;
		
		// 비밀번호: 영문자/숫자/특수문자 8~20자리
		if (password == null || !password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{1,16}$")) return false;
		
		// 이메일: 형식 체크
		if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) return false;
		
		// 이름: 한글/영문만 허용
		if (name == null || !name.matches("^[가-힣a-zA-Z]+$")) return false;
		
		// 닉네임: 한글/영문/숫자만 허용
		if (nickname == null || !nickname.matches("^[가-힣a-zA-Z0-9]+$")) return false;
		
		// 휴대폰 번호: 010으로 시작하는 숫자 11자리
		if (phonenumber == null || !phonenumber.matches("^010\\d{8}$")) return false;
		
		// 생년월일
		if (birth == null) return false;
		
		// 성별: "male" 또는 "female" (또는 "남자"/"여자"로 바꾸고 싶으면 정규식 수정)
		if (gender == null || !(gender.equalsIgnoreCase("남성") || gender.equalsIgnoreCase("여성"))) return false;
		
		return true; // 모든 조건 통과
		
	}

}
