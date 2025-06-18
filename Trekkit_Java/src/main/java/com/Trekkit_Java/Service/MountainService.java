package com.Trekkit_Java.Service;

import java.util.HashMap;

//import static org.assertj.core.api.Assertions.offset;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DAO.MountainDAO;
import com.Trekkit_Java.DTO.MountainDTO;
import com.Trekkit_Java.Model.Mountain;

@Service
public class MountainService {

	@Autowired
	private MountainDAO mountainDAO;

    public List<Mountain> getPagedAndFiltered(int page, int size, String region) {
        int offset = (page - 1) * size;
	      Map<String, Object> params = new HashMap<>();
	      params.put("offset", offset);
	      params.put("size", size);
	      params.put("region", region);
	
	      if (region != null && !region.isEmpty()) {
	          params.put("region", region);
	          return mountainDAO.selectByRegion(params);
	      } else {
	          return mountainDAO.selectAll(params);
	      }
        
        
    }
}
