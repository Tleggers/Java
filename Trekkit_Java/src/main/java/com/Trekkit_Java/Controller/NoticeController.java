package com.Trekkit_Java.Controller;

import com.Trekkit_Java.DTO.NoticeDTO;
import com.Trekkit_Java.Service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices") // API 기본 경로
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 공지사항 목록 조회 (카테고리 필터링 및 페이징)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotices(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        int offset = page * size;
        List<NoticeDTO> notices = noticeService.getNotices(category, offset, size);
        int totalCount = noticeService.getNoticeCount(category);

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("totalCount", totalCount);
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    // 공지사항 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNotice(@PathVariable("id") int id) {
        NoticeDTO notice = noticeService.getNoticeById(id);
        if (notice != null) {
            return ResponseEntity.ok(notice);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // (관리자용) 새 공지사항 작성
    @PostMapping
    public ResponseEntity<NoticeDTO> createNotice(@RequestBody NoticeDTO notice) {
        // 실제 운영 시 관리자 권한 검증 로직 필요
        NoticeDTO createdNotice = noticeService.createNotice(notice);
        return ResponseEntity.ok(createdNotice);
    }
    
    // (관리자용) 공지사항 수정
    @PutMapping("/{id}")
    public ResponseEntity<NoticeDTO> updateNotice(@PathVariable("id") int id, @RequestBody NoticeDTO notice) {
        // 실제 운영 시 관리자 권한 검증 로직 필요
        notice.setNoticeId(id);
        NoticeDTO updatedNotice = noticeService.updateNotice(notice);
        return ResponseEntity.ok(updatedNotice);
    }
    
    // (관리자용) 공지사항 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("id") int id) {
        // 실제 운영 시 관리자 권한 검증 로직 필요
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
