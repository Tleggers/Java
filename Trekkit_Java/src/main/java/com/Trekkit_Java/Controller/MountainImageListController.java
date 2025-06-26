package com.Trekkit_Java.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.Trekkit_Java.DTO.MountainImageListDTO;
import com.Trekkit_Java.Service.MountainImageListService;

@RestController
@RequestMapping("/mountainlist/image")
public class MountainImageListController {

    @Autowired
    private MountainImageListService mountainImageListService;

    @GetMapping
    public String getImageUrl(
        @RequestParam("mountain_name")String mountain_name,
        @RequestParam("location") String location
    ) {
    	System.out.println("📥 [요청] name = " + mountain_name + ", location = " + location);
        MountainImageListDTO dto = mountainImageListService.getImage(mountain_name, location);
        if (dto != null) {
            return dto.getImage_url(); // URL만 문자열로 반환
        } else {
        	System.out.println("❌ [결과 없음] 해당 산의 이미지 없음");
            return ""; // 없을 경우 빈 문자열 (또는 에러 응답으로 바꿀 수도 있음)
        }
    }
}

