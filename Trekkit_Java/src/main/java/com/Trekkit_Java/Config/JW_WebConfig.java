package com.Trekkit_Java.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JW_WebConfig implements WebMvcConfigurer { // 클래스 이름은 사용하시는 이름으로

    @Value("${file.upload.path}") // application.properties의 file.upload.path 값을 주입받음
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**" 웹 요청 경로를 실제 물리적 파일 시스템의 'uploadPath'로 매핑
        // 예: http://localhost:30000/uploads/image.jpg -> C:/trekkit_uploads/image.jpg

        // Windows 경로와 Unix-like 경로 모두 호환되도록 file:/// 접두사 사용
        String resourceLocation = "file:///" + uploadPath.replace("\\", "/");

        registry.addResourceHandler("/uploads/**") // 웹에서 /uploads/ 로 시작하는 모든 요청을 처리
                .addResourceLocations(resourceLocation); // 이 요청을 실제 uploadPath 폴더에서 찾도록 설정
    }
}