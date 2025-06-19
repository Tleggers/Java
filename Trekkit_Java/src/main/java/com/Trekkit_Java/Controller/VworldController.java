package com.Trekkit_Java.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class VworldController {

	@Value("${vworld.api.key}")
	private String VWORLD_API_KEY;

	@GetMapping("/hiking-course")
	public ResponseEntity<String> getHikingCourse(
	    @RequestParam("minLat") double minLat,
	    @RequestParam("minLng") double minLng,
	    @RequestParam("maxLat") double maxLat,
	    @RequestParam("maxLng") double maxLng
	) {
	    String url = "https://api.vworld.kr/req/data" +
	        "?service=data" +
	        "&version=2.0" +
	        "&request=GetFeature" +
	        "&format=json" +
	        "&key=" + VWORLD_API_KEY +
	        "&domain=localhost" +
	        "&data=LT_L_FRSTCLIMB" +
	        "&geomFilter=BOX(" + minLng + "," + minLat + "," + maxLng + "," + maxLat + ")" +  // ← 변경됨
	        "&size=100" +
	        "&crs=EPSG:4326";

	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        String result = restTemplate.getForObject(url, String.class);
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("브이월드 호출 실패: " + e.getMessage());
	    }
	}
}