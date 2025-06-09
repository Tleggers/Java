package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FindDAO {
	
	String findLoginTypeByEmail(@Param("email") String email); // 이메일로 로그인타입 찾기
    String findUserIdByEmail(@Param("email") String email); // 이메일로 아이디 찾기
    int updatePassword(@Param("userid") String userid, @Param("password") String password); // 비밀번호 수정
    
}
