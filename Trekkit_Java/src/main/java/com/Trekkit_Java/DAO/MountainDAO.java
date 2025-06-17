package com.Trekkit_Java.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.Trekkit_Java.Model.Mountain;

@Mapper
public interface MountainDAO {
	 void insertMountain(Mountain mountain);

//	    List<Mountain> selectByInitial(@Param("initial") String initial, @Param("offset") int offset, @Param("size") int size);
	    List<Mountain> selectAll(@Param("offset") int offset, @Param("size") int size);
//	 	List<Mountain> selectByInitial(Map<String, Object> params);
//	    List<Mountain> selectAll(Map<String, Object> params);
}
