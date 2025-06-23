package com.Trekkit_Java.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Model.Mountain;
import com.Trekkit_Java.Service.MountainBookmarkService;

@RestController
@RequestMapping("/mtbookmark")
public class MountainBookmarkController {
	
	@Autowired
	private MountainBookmarkService mtbookmarkService;
	
	@PostMapping("/add")
	public void addMtBookmark(@RequestBody Map<String, Object> req) {
		String userid = req.get("userid").toString();
        int mountainId = Integer.parseInt(req.get("mountain_id").toString());
        mtbookmarkService.addBookmark(userid, mountainId);
	}
	
	@PostMapping("/delete")
    public void deleteBookmark(@RequestBody Map<String, Object> req) {
        String userid = req.get("userid").toString();
        int mountainId = Integer.parseInt(req.get("mountain_id").toString());
        mtbookmarkService.deleteBookmark(userid, mountainId);
    }

    @PostMapping("/check")
    public boolean isBookmarked(@RequestBody Map<String, Object> req) {
        String userid = req.get("userid").toString();
        int mountainId = Integer.parseInt(req.get("mountain_id").toString());
        return mtbookmarkService.isBookmarked(userid, mountainId);
    }

    @PostMapping("/list")
    public List<Mountain> getBookmarks(@RequestBody Map<String, Object> req) {
        String userid = req.get("userid").toString();
        return mtbookmarkService.getBookmarks(userid);
    }
}
