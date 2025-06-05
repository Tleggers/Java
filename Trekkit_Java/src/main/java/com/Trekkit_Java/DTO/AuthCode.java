package com.Trekkit_Java.DTO;

import java.time.LocalDateTime;

import lombok.Data;

// 2025-06-04

@Data
public class AuthCode {
	
	private String email;
	private String code;
	private LocalDateTime created;

}
