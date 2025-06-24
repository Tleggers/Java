package com.Trekkit_Java.Service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DAO.StepDAO;
import com.Trekkit_Java.DAO.UserDAO;
import com.Trekkit_Java.DTO.Step;
import com.Trekkit_Java.DTO.StepMonthly;

@Service
public class StepService {

    @Autowired
    private StepDAO stepDAO;

    @Autowired
    private UserDAO userDAO; // ✅ 포인트 지급용

    // 🔹 일별 저장 + 포인트 지급
    public void saveDailyStep(Step step) {
        // 1. 먼저 INSERT 또는 UPDATE
        stepDAO.insertOrUpdateStep(step);

        // 2. 저장된 값 다시 조회
        Step saved = stepDAO.selectStepByUserIdAndDate(step.getUserId(), step.getWalkDate());

        // 3. 조건 확인: 1000m 이상 + 아직 보상 안됨
        if (saved.getDistance() >= 1000 && !saved.isRewarded()) {
            // 4. 포인트 지급
            userDAO.addPoint(step.getUserId(), 100);

            // 5. rewarded 상태 업데이트
            stepDAO.markRewarded(step.getUserId(), step.getWalkDate());
        }
    }

    // 🔹 일별 조회
    public Step getDailyStep(int userId, Date walkDate) {
        return stepDAO.selectStepByUserIdAndDate(userId, walkDate);
    }
    
}