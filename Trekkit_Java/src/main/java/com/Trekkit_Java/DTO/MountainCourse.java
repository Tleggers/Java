package com.Trekkit_Java.DTO;

import lombok.Data;

@Data
public class MountainCourse {
	 private String mountainName;
	    private String mountainLocation;
	    private String mountainHeight;
	    private String difficulty;
	    private String mountainIntro;
	    private String hikingCourse;
	    private String transportation;
	    private double latitude;
	    private double longitude;

	    // ✅ 기본 생성자 (MyBatis에서 필요)
	    public MountainCourse() {}
}
