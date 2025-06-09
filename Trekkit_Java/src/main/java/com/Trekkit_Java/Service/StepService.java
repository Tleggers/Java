package com.Trekkit_Java.Service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DAO.StepDAO;
import com.Trekkit_Java.DTO.Step;
import com.Trekkit_Java.DTO.StepMonthly;

@Service
public class StepService {

    @Autowired
    private StepDAO stepDAO;

    // 🔹 일별 저장
    public void saveDailyStep(Step step) {
        stepDAO.insertOrUpdateStep(step);
    }

    // 🔹 월별 저장
    public void saveMonthlyStep(StepMonthly monthly) {
        stepDAO.insertOrUpdateMonthly(monthly);
    }

    // 🔹 일별 조회
    public Step getDailyStep(int userId, Date walkDate) {
        return stepDAO.selectStepByUserIdAndDate(userId, walkDate);
    }

    // 🔹 월별 조회
    public StepMonthly getMonthlyStep(int userId, String month) {
        return stepDAO.selectMonthlyByUserIdAndMonth(userId, month);
    }
}