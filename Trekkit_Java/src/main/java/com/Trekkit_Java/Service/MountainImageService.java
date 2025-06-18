package com.Trekkit_Java.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DAO.MountainImageDAO;
import com.Trekkit_Java.DTO.MountainImageDTO;

@Service
public class MountainImageService {
	@Autowired
    private MountainImageDAO MountainImageDAO;

    public List<MountainImageDTO> getImagesByRegion(String region) {
        return MountainImageDAO.findByRegion(region);
    }
}
