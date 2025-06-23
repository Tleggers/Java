package com.Trekkit_Java.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.Trekkit_Java.Model.Mountain;

@Mapper
public interface MountainBookmarkDAO {
	void insertBookmark(@Param("userid") String userid, @Param("mountainId") int mountainId);
    void deleteBookmark(@Param("userid") String userid, @Param("mountainId") int mountainId);
    int existsBookmark(@Param("userid") String userid, @Param("mountainId") int mountainId);
    List<Mountain> selectBookmarksByUser(@Param("userid") String userid);
}
