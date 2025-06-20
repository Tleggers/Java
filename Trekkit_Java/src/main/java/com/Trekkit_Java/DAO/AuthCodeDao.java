package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Delete; // @Delete 어노테이션을 import 합니다.
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthCodeDao {

    void deleteEmail(@Param("email") String cleanEmail); 

    void insertAuthCode(@Param("email") String cleanEmail, @Param("authCode") String authCode);

//    @Delete("DELETE FROM authcode WHERE created < NOW() - INTERVAL 10 MINUTE")
    void deleteExpiredCodes();

    int verifyCode(@Param("email") String cleanEmail, @Param("code") String authcode);

}