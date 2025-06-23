package com.Trekkit_Java.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DAO.MountainDAO;
import com.Trekkit_Java.DTO.MountainDTO;
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
        String search = (String) request.get("search") != null ? request.get("search").toString() : "";
        String location = request.get("location") != null ? request.get("location").toString() : "";
        String initial = request.get("initial") != null ? request.get("initial").toString() : "";
        String sort = request.get("sort") != null ? request.get("sort").toString() : "";
        String orderBy;
        if ("asc".equals(sort)) {
            orderBy = "mntihigh ASC";
        } else if ("desc".equals(sort)) {
            orderBy = "mntihigh DESC";
        } else {
            orderBy = "mntiname ASC";
        }

        List<Mountain> data = mountainService.filteredMountains(page, size, search, location, initial, orderBy);
        int totalCount = mountainService.countByInitial(location, search, initial);
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
    
    @PostMapping("/search")
    @ResponseBody
    public List<Mountain> searchByName(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        return mountainService.searchByName(name);
    }
}