package com.Trekkit_Java.DAO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDAO {
    void addPoint(@Param("userId") int userId, @Param("point") int point);
}