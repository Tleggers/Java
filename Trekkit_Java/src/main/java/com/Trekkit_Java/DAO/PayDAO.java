package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PayDAO {

	@Update("UPDATE user SET point = point + #{point} WHERE id = #{userid}")
	int updateUserPoint(@Param("userid") Long userid,@Param("point") int point);

}
