package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Trekkit_Java.DTO.MountainCourse;

@Mapper
public interface MountainCourseDAO {
	MountainCourse findByNameAndLocation(
		    @Param("name") String name,
		    @Param("location") String location
	);
}
