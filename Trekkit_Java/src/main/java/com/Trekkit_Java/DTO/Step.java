package com.Trekkit_Java.DTO;

import java.sql.Date;

import lombok.Data;

//0609

@Data
public class Step {
    private int id;
    private int userId;
    private int distance;
    private Date created;
    private Date updated;
    private java.sql.Date walkDate;
    private boolean rewarded;
}
