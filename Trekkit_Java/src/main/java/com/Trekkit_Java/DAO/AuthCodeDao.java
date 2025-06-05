package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// 2025-06-04 완

@Mapper
public interface AuthCodeDao {

	void deleteEmail(@Param("email") String cleanEmail); 

	void insertAuthCode(@Param("email") String cleanEmail, @Param("authCode") String authCode);

	void deleteExpiredCodes();

	int verifyCode(@Param("email") String cleanEmail, @Param("code") String authcode);

}
