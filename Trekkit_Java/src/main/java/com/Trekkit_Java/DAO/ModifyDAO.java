package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ModifyDAO {

	// 비밀번호를 포함한 수정
	int updateData(@Param("id") Long userId,@Param("pw") String hashedPw,
						   @Param("nickname") String nickname,@Param("profile") String profile);

	// 비밀번호가 없는 수정
	int updateDataNP(@Param("id") Long userId,@Param("nickname") String nickname,@Param("profile") String profile);

	// 삭제
	int deleteUser(@Param("id") Long userId);

}
