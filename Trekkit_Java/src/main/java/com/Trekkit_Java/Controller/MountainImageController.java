package com.Trekkit_Java.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DTO.MountainImageDTO;
import com.Trekkit_Java.Service.MountainImageService;

@RestController
@RequestMapping("/mountainimage")
public class MountainImageController {

    @Autowired
    private MountainImageService service;

    @GetMapping("/images")
    public List<MountainImageDTO> getImagesByRegion(@RequestParam("region") String region) {
        return service.getImagesByRegion(region);
    }
}
