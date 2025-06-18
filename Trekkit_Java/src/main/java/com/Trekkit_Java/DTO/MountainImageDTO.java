package com.Trekkit_Java.DTO;

import lombok.Data;

@Data
public class MountainImageDTO {
	private Long id;
    private String name;
    private String region;
    private Double latitude;
    private Double longitude;
    private String image_url;
}
