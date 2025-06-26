package com.Trekkit_Java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DAO.MountainImageListDAO;
import com.Trekkit_Java.DTO.MountainImageListDTO;

@Service
public class MountainImageListService {

    @Autowired
    private MountainImageListDAO mountainImageListDAO;

    // 산 이름 + 위치로 이미지 DTO 조회
    public MountainImageListDTO getImage(String mountain_name, String location) {
        return mountainImageListDAO.findByImage(mountain_name, location);
    }
}