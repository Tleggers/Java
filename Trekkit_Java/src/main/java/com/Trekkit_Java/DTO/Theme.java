package com.Trekkit_Java.DTO;

import lombok.Data;

@Data
public class Theme {
	
	// 테마 정보
	private Long theme_id;
    private String theme_name;
    private String subtitle;
    private String theme_description;
    private String theme_image;

    // 산1 + 코스1
    private Long mountain1_id;
    private String mountain1_name;
    private String mountain1_image;
    private String mountain1_region;
    private String mountain1_difficulty;
    private Integer mountain1_duration;
    private Double mountain1_height;
    private String course1_name;
    private String course1_description;
    private String course1_route;
    private String course1_note;

    // 산2 + 코스2
    private Long mountain2_id;
    private String mountain2_name;
    private String mountain2_image;
    private String mountain2_region;
    private String mountain2_difficulty;
    private Integer mountain2_duration;
    private Double mountain2_height;
    private String course2_name;
    private String course2_description;
    private String course2_route;
    private String course2_note;
   
    // 산3 + 코스3
    private Long mountain3_id;
    private String mountain3_name;
    private String mountain3_image;
    private String mountain3_region;
    private String mountain3_difficulty;
    private Integer mountain3_duration;
    private Double mountain3_height;
    private String course3_name;
    private String course3_description;
    private String course3_route;
    private String course3_note;

}
