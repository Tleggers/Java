package com.Trekkit_Java.Controller;

import com.Trekkit_Java.DTO.NoticeDTO;
import com.Trekkit_Java.DTO.User;
import com.Trekkit_Java.DAO.LoginDAO;
import com.Trekkit_Java.Service.NoticeService;
import com.Trekkit_Java.Util.ExtractToken;
import com.Trekkit_Java.Util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LoginDAO loginDAO;

    private int getAdminUserIdFromRequest(HttpServletRequest request) {
        String token = ExtractToken.extractToken(request);
        if (token == null) {
            throw new IllegalArgumentException("JWT 토큰이 없습니다. 로그인 상태를 확인해주세요.");
        }
        Long userId = jwtUtil.extractUserId(token);
        User user = loginDAO.findById(userId);
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getUsertype())) {
            throw new SecurityException("관리자 권한이 없습니다.");
        }
        return userId.intValue();
    }

    @PostMapping("/list")
    public ResponseEntity<Map<String, Object>> getNoticesByPost(@RequestBody Map<String, Integer> requestBody) {
        try {
            int page = requestBody.getOrDefault("page", 0);
            int size = requestBody.getOrDefault("size", 10);

            Map<String, Object> responseData = noticeService.getNotices(page, size);
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "공지사항 조회 실패: " + e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNotice(@PathVariable("id") int id) {
        try {
            NoticeDTO notice = noticeService.getNoticeDetail(id);
            if (notice != null) {
                return ResponseEntity.ok(notice);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 기존 @PostMapping (공지사항 생성)은 그대로 유지합니다.
    @PostMapping // HTTP POST 요청을 처리합니다. (기존 공지사항 생성 로직)
    public ResponseEntity<String> createNotice(@RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        try {
            int adminUserId = getAdminUserIdFromRequest(request);
            noticeDTO.setUserId(adminUserId);

            noticeService.createNotice(noticeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("공지사항이 성공적으로 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 생성 실패: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateNotice(@PathVariable("id") int id, @RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        try {
            int adminUserId = getAdminUserIdFromRequest(request);

            NoticeDTO existingNotice = noticeService.getNoticeDetail(id);
            if (existingNotice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항을 찾을 수 없습니다.");
            }
            if (existingNotice.getUserId() != null && existingNotice.getUserId() != adminUserId) {
                throw new SecurityException("공지사항 수정 권한이 없습니다: 작성자 불일치.");
            }

            noticeDTO.setNoticeId(id);
            noticeDTO.setUserId(existingNotice.getUserId());

            noticeService.updateNotice(noticeDTO);
            return ResponseEntity.ok("공지사항이 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 수정 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotice(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            int adminUserId = getAdminUserIdFromRequest(request);

            NoticeDTO existingNotice = noticeService.getNoticeDetail(id);
            if (existingNotice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항을 찾을 수 없습니다.");
            }
            if (existingNotice.getUserId() != null && existingNotice.getUserId() != adminUserId) {
                throw new SecurityException("공지사항 삭제 권한이 없습니다: 작성자 불일치.");
            }

            noticeService.deleteNotice(id);
            return ResponseEntity.ok("공지사항이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 삭제 실패: " + e.getMessage());
        }
    }
}