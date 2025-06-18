package com.Trekkit_Java.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.Trekkit_Java.DTO.MountainImageDTO;

@Mapper
public interface MountainImageDAO {
	List<MountainImageDTO> findByRegion(String region);
}
