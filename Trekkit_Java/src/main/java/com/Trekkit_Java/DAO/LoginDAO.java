package com.Trekkit_Java.DAO;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Trekkit_Java.DTO.User;

// 2025-06-05 완

@Mapper
public interface LoginDAO {

	Long findByUserid(@Param("userid") String cleanid);

	User findById(@Param("id") long id);

	Map<String, Object> findUserByEmail(@Param("email") String email);

	int insertKakaoUser(@Param("email") String email,@Param("nickname") String nickname,
							@Param("profile") String profile,@Param("type") String type);

	User findIdByEmail(@Param("email") String email);

}
