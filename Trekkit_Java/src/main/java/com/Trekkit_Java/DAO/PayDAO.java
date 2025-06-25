package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PayDAO {

	@Update("UPDATE user SET point = point + #{point} WHERE id = #{userid}")
	int updateUserPoint(@Param("userid") Long userid,@Param("point") int point);
	
	// 포인트 차감 (포인트가 충분한 경우에만 실행됨)
    @Update("UPDATE user SET point = point - #{point} WHERE id = #{userid} AND point >= #{point}")
    int usePoint(@Param("userid") Long userid, @Param("point") int point);

}
