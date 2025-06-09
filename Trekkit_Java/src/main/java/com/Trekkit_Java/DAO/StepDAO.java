package com.Trekkit_Java.DAO;

import java.sql.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.Trekkit_Java.DTO.Step;
import com.Trekkit_Java.DTO.StepMonthly;

@Mapper
public interface StepDAO {
    // 🔹 하루 기록 저장 (INSERT 또는 UPDATE)
    int insertOrUpdateStep(Step step);

    // 🔹 월별 누적 기록 저장 (INSERT 또는 UPDATE)
    int insertOrUpdateMonthly(StepMonthly monthly);

    // 🔹 특정 유저의 일별 기록 조회
    Step selectStepByUserIdAndDate(@Param("userId") int userId, @Param("walkDate") Date walkDate);

    // 🔹 특정 유저의 월별 누적 조회
    StepMonthly selectMonthlyByUserIdAndMonth(@Param("userId") int userId, @Param("month") String month);
}
