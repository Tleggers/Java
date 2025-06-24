package com.Trekkit_Java.Controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DTO.Step;
import com.Trekkit_Java.Service.StepService;

@RestController
@RequestMapping("/step")
public class StepController {

    @Autowired
    private StepService stepService;

    // 🔹 일별 걸음수 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveStep(@RequestBody Step step) {
        stepService.saveDailyStep(step);
        return ResponseEntity.ok("일별 걸음수 저장 완료");
    }

    // 🔹 특정 날짜 걸음수 조회
    @GetMapping("/daily")
    public Step getDaily(@RequestParam("userId") int userId,
            				@RequestParam("walkDate") String walkDate) {
        return stepService.getDailyStep(userId, Date.valueOf(walkDate));
    }
} 

