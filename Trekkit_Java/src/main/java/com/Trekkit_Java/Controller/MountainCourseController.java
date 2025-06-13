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

	    @GetMapping("/findByName")
	    public ResponseEntity<?> findByName(@RequestParam("name") String name) {
	    	// ✅ 디버깅용 로그 출력
	        System.out.println("💡 받은 name 파라미터: " + name);
	        
	        MountainCourse course = Mcourse.findByMountainName(name);

	        if (course == null) {
	            System.out.println("❗ DB에서 해당 산 찾지 못함: " + name);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("산 정보를 찾을 수 없습니다.");
	        }

	        System.out.println("✅ DB에서 매칭된 산 정보: " + course.getMountainName());
	        return ResponseEntity.ok(course);
	    }
}
