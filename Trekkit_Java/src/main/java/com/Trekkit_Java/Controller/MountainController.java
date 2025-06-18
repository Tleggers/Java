package com.Trekkit_Java.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Model.Mountain;
import com.Trekkit_Java.Service.MountainService;

@RestController
@RequestMapping("/mountains")
public class MountainController {

    @Autowired
    private MountainService mountainService;
    
    @GetMapping("/list")
    public List<Mountain> getMountainsByRegion(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String region
        ) {
            return mountainService.getPagedAndFiltered(page, size, region);
        }
    }