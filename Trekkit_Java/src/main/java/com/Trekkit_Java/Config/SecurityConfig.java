package com.Trekkit_Java.Config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// 2025-06-05 미완

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	 @Value("${cors.allowed-origins}")
	 private String allowedOrigins;
	
	// CSRF설정
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
	    http
	    	.cors(cors -> corsConfigurationSource())
	        .csrf(csrf -> csrf.disable())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
	        .authorizeHttpRequests(auth -> auth
	        		.requestMatchers("/modify/**").authenticated()
	                .anyRequest().permitAll() // 나머지는 인증 불필요
	        )
	        .oauth2Login(oauth -> oauth
	        	    .loginPage("/login")  // 명시적 로그인 페이지
	        	    .defaultSuccessUrl("/login/oauth2/success", true)
    	  )
	    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	// Cors설정
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ properties에서 불러온 문자열을 List<String>으로 변환
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(origins);
        
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true); // 쿠키/인증 정보 허용

        // Api주소 집어넣기
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/signup/**", config); // 회원가입
        source.registerCorsConfiguration("/login/**", config); // 로그인
        source.registerCorsConfiguration("/find/**", config); // 아이디 비밀번호 찾기
        source.registerCorsConfiguration("/modify/**", config); // 수정 페이지
        source.registerCorsConfiguration("/mountains", config);
        source.registerCorsConfiguration("/mountains/**", config);
        source.registerCorsConfiguration("/apis.data.go.kr/**", config);
        source.registerCorsConfiguration("/step/**", config); // 기쁨이꺼
        source.registerCorsConfiguration("/api/**", config); // 진우형
        source.registerCorsConfiguration("/login/sociallogin", config); // 로그인 redirect
        source.registerCorsConfiguration("/mountains", config);
        source.registerCorsConfiguration("/mountains/**", config);
        source.registerCorsConfiguration("/mountainimage/**", config); 
        source.registerCorsConfiguration("/apis.data.go.kr/**", config);
        source.registerCorsConfiguration("/theme/**", config);
        source.registerCorsConfiguration("/hiking-course", config);

        // 구글
        source.registerCorsConfiguration("/oauth2/**", config);
        source.registerCorsConfiguration("/login/oauth2/**", config); // success 리디렉션 받을 URI

        return source;
    }

}
