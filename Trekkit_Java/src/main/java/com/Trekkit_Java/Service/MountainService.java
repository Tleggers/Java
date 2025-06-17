package com.Trekkit_Java.Service;

import java.util.HashMap;
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

	public void saveAll(List<MountainDTO> mountainDTOs) {
        for (MountainDTO dto : mountainDTOs) {
            Mountain m = new Mountain();
            m.setMntiListNo(dto.getMntiListNo());
            m.setName(dto.getName());
            m.setTopReason(dto.getTopReason());
            m.setSubName(dto.getSubName());
            m.setLocation(dto.getLocation());
            m.setHeight(dto.getHeight());
            m.setSummary(dto.getSummary());
            m.setDetails(dto.getDetails());
            mountainDAO.insertMountain(m);
        }
    }

    public List<Mountain> getPagedAndFiltered(int page, int size, String initial) {
        int offset = (page - 1) * size;
        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("size", size);

//        if (initial != null && !initial.isEmpty()) {
//            params.put("initial", initial);
//            return mountainDAO.selectByInitial(params);
//        } else {
//            return mountainDAO.selectAll(params);
//        }
//        if (initial != null && !initial.isEmpty()) {
//        	System.out.println("initial = " + initial + ", page = " + page + ", size = " + size);
//            return mountainDAO.selectByInitial(initial, offset, size);
//        } else {
            return mountainDAO.selectAll(offset, size);
//        }
    }
}
