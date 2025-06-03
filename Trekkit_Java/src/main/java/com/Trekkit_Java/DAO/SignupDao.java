package com.Trekkit_Java.DAO;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SignupDao {

	int checkDupId(@Param("userid") String userid);

	int checkDupE(@Param("email") String email);

	int checkDupNickName(@Param("nickname") String nickname);

	int checkDupMobile(@Param("mobile") String enmobile);

	int doSignup(@Param("userid") String cuserid,
				    @Param("password") String hashedPw,
				    @Param("email") String cemail, 
				    @Param("name") String cname, 
				    @Param("nickname") String cnickname,
				    @Param("birth") LocalDate birth,
				    @Param("number") String cnumber,
				    @Param("gender") String gender);

}
