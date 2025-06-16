package com.Trekkit_Java.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DTO.MountainDTO;
import com.Trekkit_Java.ExternalAPI.MountainAPIClient;

@Service
public class MountainService {

	@Autowired
    private MountainAPIClient apiClient;

    public List<MountainDTO> fetchMountains() {
        return apiClient.callMountainAPI();
    }
}
