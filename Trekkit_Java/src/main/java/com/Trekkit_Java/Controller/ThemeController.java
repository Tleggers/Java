package com.Trekkit_Java.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DAO.ThemeDAO;
import com.Trekkit_Java.DTO.Theme;
import com.Trekkit_Java.Service.ThemeService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/theme")
public class ThemeController {
	
	@Autowired private ThemeService themeService;
	@Autowired private ThemeDAO td;

	@PostMapping("/detail")
	public ResponseEntity<Theme> getThemeDetail(@RequestBody Map<String, String> body) {
	    String name = body.get("name");
	    if (name == null || name.trim().isEmpty()) {
	        return ResponseEntity.badRequest().build();
	    }

	    Theme dto = themeService.getThemeDetail(name);
	    if (dto == null) return ResponseEntity.notFound().build();
	    return ResponseEntity.ok(dto);
	}
	
	@PostMapping("/getall")
	public ResponseEntity<?> getAllThemesPost(HttpServletRequest request) {
		
	    String clientType = request.getHeader("X-Client-Type");

	    // 클라이언트 타입 체크 (선택 사항)
	    if (clientType == null || !clientType.equals("web")) {
	        return ResponseEntity.status(400).body(Map.of("error", "Invalid client type"));
	    }

	    return ResponseEntity.ok(td.getThemes());
	}

}
