package com.Trekkit_Java;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.Trekkit_Java.DAO")
@MapperScan(basePackages = "com.Trekkit_Java.repository")
public class TrekkitJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrekkitJavaApplication.class, args);
	}

}
