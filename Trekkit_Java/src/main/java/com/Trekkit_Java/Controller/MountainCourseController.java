package com.Trekkit_Java.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DAO.MountainCourseDAO;
import com.Trekkit_Java.DTO.MountainCourse;

@RestController
@RequestMapping("/mountaincourse")
public class MountainCourseController {
	 private final MountainCourseDAO Mcourse;

	    public MountainCourseController(MountainCourseDAO Mcourse) {
	        this.Mcourse = Mcourse;
	    }

	    @GetMapping("/findByNameAndLocation")
	    public ResponseEntity<?> findByName(@RequestParam("name") String name, @RequestParam("location") String location) {
	    	// ✅ 디버깅용 로그 출력
	    	 System.out.println("💡 받은 name: " + name + ", location: " + location);
	        
	    	 MountainCourse course = Mcourse.findByNameAndLocation(name, location);

	    	 if (course == null) {
	    	        System.out.println("❗ DB에서 해당 산을 찾지 못함");
	    	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	    	                .body("산 정보를 찾을 수 없습니다.");
	    	    }

	    	    System.out.println("✅ DB 매칭 성공: " + course.getMountainName());
	    	    return ResponseEntity.ok(course);
	    	}
}
