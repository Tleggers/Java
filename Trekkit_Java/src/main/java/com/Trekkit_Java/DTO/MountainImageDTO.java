package com.Trekkit_Java.DTO;

import lombok.Data;

@Data
public class MountainImageDTO {
	private Long id;
    private String name;
    private String region;
    private String location;
    private String height;
    private String tip;
    private String warning;
    private Double latitude;
    private Double longitude;
    private String image_url;
}
