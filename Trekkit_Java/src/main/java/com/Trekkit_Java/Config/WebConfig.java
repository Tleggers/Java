package com.Trekkit_Java.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.properties의 file.upload.path 값을 주입받음
    @Value("${file.upload.path}")
    private String generalUploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. /profile/** 요청에 대한 리소스 핸들러
        registry
            .addResourceHandler("/profile/**")
            .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/profile/");

        // 2. /uploads/** 요청에 대한 리소스 핸들러 (JW_WebConfig에서 가져온 기능)
        // Windows와 Unix 계열 경로 호환을 위해 "file:///" 접두사 사용
        String resourceLocation = "file:///" + generalUploadPath.replace("\\", "/");

        registry
            .addResourceHandler("/uploads/**") // 웹에서 /uploads/로 시작하는 모든 요청을 처리
            .addResourceLocations(resourceLocation); // 실제 generalUploadPath 폴더에서 파일을 찾도록 설정
    }
}
