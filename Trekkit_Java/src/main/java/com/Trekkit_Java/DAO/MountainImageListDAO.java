package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Trekkit_Java.DTO.MountainImageListDTO;

@Mapper
public interface MountainImageListDAO {
	// 산 이름 + 소재지로 이미지 URL 조회
	MountainImageListDTO findByImage(
		@Param("mountain_name") String mountain_name,
		@Param("location") String location
    );
}
