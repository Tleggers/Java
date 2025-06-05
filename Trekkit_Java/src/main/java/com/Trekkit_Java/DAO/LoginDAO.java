package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Trekkit_Java.DTO.User;

@Mapper
public interface LoginDAO {

	Long findByUserid(@Param("userid") String cleanid);

	User findById(@Param("id") long id);

}
