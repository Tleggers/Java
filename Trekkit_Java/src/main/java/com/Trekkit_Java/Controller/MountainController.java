package com.Trekkit_Java.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DAO.MountainDAO;
import com.Trekkit_Java.DTO.MountainDTO;
import com.Trekkit_Java.ExternalAPI.MountainAPIClient;
import com.Trekkit_Java.Model.Mountain;
import com.Trekkit_Java.Service.MountainService;

@RestController
@RequestMapping("/mountains")
public class MountainController {

    @Autowired
    private MountainService mountainService;
    
    @Autowired
    private MountainAPIClient apiClient;
    
    @Autowired
    private MountainDAO mountainDAO;

	@GetMapping("/load-mountains")
	public String loadMountains() {
	    List<MountainDTO> dtoList = apiClient.callMountainAPI();
	    mountainService.saveAll(dtoList);
	    return "산 데이터 저장 완료!";
	}

    @GetMapping("/test")
    public List<Mountain> test() {
    	List<Mountain> result = mountainDAO.selectAll(0, 10);
        System.out.println("조회된 산 목록 수: " + result.size());
        for (Mountain m : result) {
            System.out.println("산 이름: " + m.getName());
        }
        return result;
    }
    
    // 저장용 API (한 번만 호출)
//    @PostMapping("/save")
//    public String saveMountainData() {
//        List<MountainDTO> apiData = apiClient.callMountainAPI();
//        mountainService.saveAll(apiData);
//        return "산 데이터 저장 완료 (" + apiData.size() + "개)";
//    }
//
//    @GetMapping
//    public List<Mountain> getMountains(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "100") int size,
//            @RequestParam(required = false) String initial
//        ) {
//            return mountainService.getPagedAndFiltered(page, size, initial);
//        }
    }