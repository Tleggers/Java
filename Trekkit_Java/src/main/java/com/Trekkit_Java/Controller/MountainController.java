package com.Trekkit_Java.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Model.Mountain;
import com.Trekkit_Java.Service.MountainService;

@RestController
@RequestMapping("/mountains")
public class MountainController {

    @Autowired
    private MountainService mountainService;
    
    @PostMapping("/list")
    public Map<String, Object> getMountains(@RequestBody Map<String, Object> request) {
        int page = Integer.parseInt(request.get("page").toString());
        int size = Integer.parseInt(request.get("size").toString());
        String location = request.get("location") != null ? request.get("location").toString() : "";
        String initial = request.get("initial") != null ? request.get("initial").toString() : "";

        List<Mountain> data = mountainService.getPagedAndFiltered(page, size, location, initial);
        int totalCount = mountainService.countByInitial(location, initial);

        int totalPages = (int) Math.ceil((double) totalCount / size);

        Map<String, Object> result = new HashMap<>();
        result.put("mountains", data);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);

        return result;
    }
    
    @PostMapping("/detail")
    @ResponseBody
    public Mountain getMountainDetail(@RequestBody Map<String, Object> request) {
    	Object noObj = request.get("mntilistno");
    	if (noObj == null) {
    	    throw new IllegalArgumentException("mntilistno is missing");
    	}
    	int mntilistno = Integer.parseInt(noObj.toString());
        return mountainService.getMountainByListNo(mntilistno);
    }
}