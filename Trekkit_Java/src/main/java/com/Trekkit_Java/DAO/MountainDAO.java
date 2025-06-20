package com.Trekkit_Java.DAO;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.Trekkit_Java.Model.Mountain;

@Mapper
public interface MountainDAO {
	 void insertMountain(Mountain mountain);
	 
	 	List<Mountain> filteredMountains(Map<String, Object> params);
	    List<Mountain> selectAll(Map<String, Object> params);
	    Mountain selectByListNo(int mntilistno);
	    List<Mountain> searchByName(String name);
	    int countAll();
	    int countByLocation(String location);
		int countByCondition(Map<String, Object> params);
}
