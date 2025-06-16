package com.Trekkit_Java.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DTO.MountainDTO;
import com.Trekkit_Java.Service.MountainService;

@RestController
@RequestMapping("/mountains")
public class MountainController {

    @Autowired
    private MountainService mountainService;

    @GetMapping
    public List<MountainDTO> getMountainList() {
        return mountainService.fetchMountains();
    }
}
