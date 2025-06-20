package com.Trekkit_Java.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DAO.MountainDAO;
import com.Trekkit_Java.Model.Mountain;

@Service
public class MountainService {

	@Autowired
	private MountainDAO mountainDAO;
	
	public List<Mountain> getAllMountains(int page, int size) {
	    int offset = (page - 1) * size;

	    Map<String, Object> params = new HashMap<>();
	    params.put("offset", offset);
	    params.put("size", size);

	    return mountainDAO.selectAll(params);
	}

    public List<Mountain> filteredMountains(int page, int size, String search, String location, String initial) {
        int offset = (page - 1) * size;
        
	      Map<String, Object> params = new HashMap<>();
	      params.put("offset", offset);
	      params.put("size", size);
	      params.put("search", search);
	
	      if (location != null && !location.trim().isEmpty()) {
	    	  params.put("location", location);
	      }
	      
	      if (initial != null && !initial.isEmpty()) {
	            switch (initial) {
	                case "ㄱ": params.put("initialStart", "가"); params.put("initialEnd", "나"); break;
	                case "ㄴ": params.put("initialStart", "나"); params.put("initialEnd", "다"); break;
	                case "ㄷ": params.put("initialStart", "다"); params.put("initialEnd", "라"); break;
	                case "ㄹ": params.put("initialStart", "라"); params.put("initialEnd", "마"); break;
	                case "ㅁ": params.put("initialStart", "마"); params.put("initialEnd", "바"); break;
	                case "ㅂ": params.put("initialStart", "바"); params.put("initialEnd", "사"); break;
	                case "ㅅ": params.put("initialStart", "사"); params.put("initialEnd", "아"); break;
	                case "ㅇ": params.put("initialStart", "아"); params.put("initialEnd", "자"); break;
	                case "ㅈ": params.put("initialStart", "자"); params.put("initialEnd", "차"); break;
	                case "ㅊ": params.put("initialStart", "차"); params.put("initialEnd", "카"); break;
	                case "ㅋ": params.put("initialStart", "카"); params.put("initialEnd", "타"); break;
	                case "ㅌ": params.put("initialStart", "타"); params.put("initialEnd", "파"); break;
	                case "ㅍ": params.put("initialStart", "파"); params.put("initialEnd", "하"); break;
	                case "ㅎ": params.put("initialStart", "하"); params.put("initialEnd", "힣"); break;
	            }
	            params.put("initial", initial);
	        }
	     return mountainDAO.filteredMountains(params);
    }
    
    public Mountain getMountainByListNo(int mntilistno) {
        return mountainDAO.selectByListNo(mntilistno);
    }
    
    public List<Mountain> searchByName(String name) {
        return mountainDAO.searchByName(name);
    }
    
    public int countAll() {
        return mountainDAO.countAll();
    }

    public int countByLocation(String location) {
        return mountainDAO.countByLocation(location);
    }
    
    public int countByInitial(String location, String search, String initial) {
    	    Map<String, Object> params = new HashMap<>();

    	    if (location != null && !location.isEmpty()) {
    	        params.put("location", location);
    	    }
    	    
    	    if (search != null && !search.isEmpty()) {
    	        params.put("search", search);
    	    }

    	    if (initial != null && !initial.isEmpty()) {
                switch (initial) {
                    case "ㄱ": params.put("initialStart", "가"); params.put("initialEnd", "나"); break;
                    case "ㄴ": params.put("initialStart", "나"); params.put("initialEnd", "다"); break;
                    case "ㄷ": params.put("initialStart", "다"); params.put("initialEnd", "라"); break;
                    case "ㄹ": params.put("initialStart", "라"); params.put("initialEnd", "마"); break;
                    case "ㅁ": params.put("initialStart", "마"); params.put("initialEnd", "바"); break;
                    case "ㅂ": params.put("initialStart", "바"); params.put("initialEnd", "사"); break;
                    case "ㅅ": params.put("initialStart", "사"); params.put("initialEnd", "아"); break;
                    case "ㅇ": params.put("initialStart", "아"); params.put("initialEnd", "자"); break;
                    case "ㅈ": params.put("initialStart", "자"); params.put("initialEnd", "차"); break;
                    case "ㅊ": params.put("initialStart", "차"); params.put("initialEnd", "카"); break;
                    case "ㅋ": params.put("initialStart", "카"); params.put("initialEnd", "타"); break;
                    case "ㅌ": params.put("initialStart", "타"); params.put("initialEnd", "파"); break;
                    case "ㅍ": params.put("initialStart", "파"); params.put("initialEnd", "하"); break;
                    case "ㅎ": params.put("initialStart", "하"); params.put("initialEnd", "힣"); break;
                }
                params.put("initial", initial);
            }
            return mountainDAO.countByCondition(params);
        }
}
