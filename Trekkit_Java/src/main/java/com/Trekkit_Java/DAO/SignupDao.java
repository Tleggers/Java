package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 2025-06-05 완

@Mapper
public interface SignupDao {

	int checkDupId(@Param("userid") String userid); 

	int checkDupE(@Param("email") String email);

	int checkDupNickName(@Param("nickname") String nickname);

	int checkDupMobile(@Param("mobile") String enmobile);

	int doSignup(@Param("userid") String cuserid,
				    @Param("password") String hashedPw,
				    @Param("email") String cemail, 
				    @Param("nickname") String cnickname,
				    @Param("image") String imageUrl);

	int existsByIdAndEmail(@Param("userid") String userid, @Param("email") String email);

}
