package com.Trekkit_Java.DTO;

import lombok.Data;

@Data
public class MountainImageListDTO {
	 private Long id;
	 private String mountain_name;
	 private String location;
	 private String image_url;

	 // ✅ 기본 생성자 (MyBatis 필수)
	 public MountainImageListDTO() {}
}
