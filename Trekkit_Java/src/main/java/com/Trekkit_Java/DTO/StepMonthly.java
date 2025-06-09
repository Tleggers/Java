package com.Trekkit_Java.DTO;

import java.sql.Date;

import lombok.Data;

@Data
public class StepMonthly {
    private long id;
    private int userId;
    private String month; // "2025-06" 형식
    private int totalDistance;
    private Date created;
    private Date updated;
}
