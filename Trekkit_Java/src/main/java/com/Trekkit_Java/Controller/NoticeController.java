package com.Trekkit_Java.Controller;

import com.Trekkit_Java.DTO.NoticeDTO; // 공지사항 데이터 전송 객체(DTO) 임포트
import com.Trekkit_Java.DTO.User; // 사용자 정보를 담는 DTO 임포트 (관리자 권한 확인용)
import com.Trekkit_Java.DAO.LoginDAO; // 사용자 DAO 임포트 (사용자 조회용)
import com.Trekkit_Java.Service.NoticeService; // 공지사항 관련 비즈니스 로직 서비스 임포트
import com.Trekkit_Java.Util.ExtractToken; // JWT 토큰 추출 유틸리티 임포트
import com.Trekkit_Java.Util.JwtUtil; // JWT 유틸리티 (토큰 파싱 등) 임포트
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 정보 접근을 위한 임포트
import org.springframework.beans.factory.annotation.Autowired; // Spring의 의존성 자동 주입 어노테이션
import org.springframework.http.HttpStatus; // HTTP 상태 코드 사용을 위한 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답 객체 생성을 위한 임포트
import org.springframework.web.bind.annotation.*; // Spring Web 관련 어노테이션들 임포트

import java.util.List; // List 인터페이스 사용
import java.util.Map; // Map 인터페이스 사용

/**
 * 공지사항 관련 API 요청을 처리하는 Spring REST 컨트롤러입니다.
 * 모든 요청은 "/api/notices" 경로로 매핑됩니다.
 * 관리자 권한이 필요한 API는 JWT 토큰을 통해 사용자 인증 및 권한을 확인합니다.
 */
@RestController // 이 클래스가 RESTful 웹 서비스의 컨트롤러임을 나타냅니다.
@RequestMapping("/api/notices") // 이 컨트롤러의 모든 핸들러 메서드에 대한 기본 경로를 설정합니다.
public class NoticeController {

    @Autowired // NoticeService를 자동으로 주입받습니다.
    private NoticeService noticeService;

    @Autowired // JwtUtil을 자동으로 주입받습니다.
    private JwtUtil jwtUtil;

    @Autowired // LoginDAO를 자동으로 주입받습니다.
    private LoginDAO loginDAO;

    /**
     * 현재 HTTP 요청에서 JWT 토큰을 추출하고, 토큰에서 사용자 ID를 얻은 후,
     * 해당 사용자가 관리자(`ADMIN`) 권한을 가지고 있는지 확인합니다.
     *
     * @param request 현재 HTTP 요청 객체.
     * @return 관리자 사용자의 ID (int 타입).
     * @throws IllegalArgumentException JWT 토큰이 없거나 유효하지 않을 경우 발생.
     * @throws SecurityException 관리자 권한이 없거나 사용자를 찾을 수 없을 경우 발생.
     */
    private int getAdminUserIdFromRequest(HttpServletRequest request) {
        String token = ExtractToken.extractToken(request); // 요청에서 JWT 토큰을 추출합니다.
        if (token == null) {
            // 토큰이 없으면 로그인 필요 예외를 던집니다.
            throw new IllegalArgumentException("JWT 토큰이 없습니다. 로그인 상태를 확인해주세요.");
        }
        // JWT 토큰에서 사용자 ID를 추출합니다.
        Long userId = jwtUtil.extractUserId(token);
        // 추출된 userId로 데이터베이스에서 사용자 정보를 조회합니다.
        User user = loginDAO.findById(userId);
        // 사용자가 없거나, 사용자 타입이 'ADMIN'이 아니면 권한 없음 예외를 던집니다.
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getUsertype())) {
            throw new SecurityException("관리자 권한이 없습니다.");
        }
        // 관리자 ID를 int 타입으로 반환합니다.
        return userId.intValue();
    }

    /**
     * 페이징 처리된 공지사항 목록을 조회합니다.
     * 클라이언트로부터 페이지 번호와 페이지 크기를 요청 본문으로 받아 처리합니다.
     *
     * @param requestBody 요청 본문 (페이지 번호 `page`, 페이지 크기 `size` 포함).
     * @return 공지사항 목록과 전체 개수(`totalCount`)를 포함하는 Map과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/list") // POST 요청을 "/api/notices/list" 경로로 매핑합니다.
    public ResponseEntity<Map<String, Object>> getNoticesByPost(@RequestBody Map<String, Integer> requestBody) {
        try {
            // 요청 본문에서 페이지 번호(`page`)와 페이지 크기(`size`)를 추출하고 기본값을 설정합니다.
            int page = requestBody.getOrDefault("page", 0);
            int size = requestBody.getOrDefault("size", 10);

            // NoticeService를 통해 공지사항 목록과 총 개수를 가져옵니다.
            Map<String, Object> responseData = noticeService.getNotices(page, size);
            // HTTP 200 OK 상태 코드와 함께 응답 데이터를 반환합니다.
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
            // 서버 내부 오류 응답과 함께 오류 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "공지사항 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 ID를 가진 공지사항의 상세 정보를 조회합니다.
     *
     * @param id 조회할 공지사항의 고유 ID (경로 변수).
     * @return 조회된 공지사항 정보(NoticeDTO) 또는 HTTP 404 Not Found, HTTP 500 Internal Server Error 응답을 반환합니다.
     */
    @GetMapping("/{id}") // GET 요청을 "/api/notices/{id}" 경로로 매핑합니다.
    public ResponseEntity<NoticeDTO> getNotice(@PathVariable("id") int id) {
        try {
            // NoticeService를 통해 공지사항 상세 정보를 가져옵니다.
            NoticeDTO notice = noticeService.getNoticeDetail(id);
            if (notice != null) {
                // 공지사항을 찾으면 HTTP 200 OK 상태 코드와 함께 공지사항 정보를 반환합니다.
                return ResponseEntity.ok(notice);
            } else {
                // 공지사항을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // 공지사항 조회 중 예외 발생 시 HTTP 500 Internal Server Error 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 새로운 공지사항을 생성합니다.
     * 이 API는 관리자만 접근할 수 있으며, JWT 토큰으로 관리자 권한을 확인합니다.
     *
     * @param noticeDTO 생성할 공지사항 정보를 담고 있는 NoticeDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 공지사항 생성 성공 또는 실패 메시지와 함께 HTTP 응답을 반환합니다.
     */
    @PostMapping // POST 요청을 "/api/notices" 경로로 매핑합니다.
    public ResponseEntity<String> createNotice(@RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        try {
            // 요청에서 관리자 사용자 ID를 추출하고 권한을 확인합니다. (권한 없으면 예외 발생)
            int adminUserId = getAdminUserIdFromRequest(request);
            // 공지사항 DTO에 관리자 사용자 ID를 설정합니다.
            noticeDTO.setUserId(adminUserId);

            // NoticeService를 통해 공지사항을 생성합니다.
            noticeService.createNotice(noticeDTO);
            // 공지사항 생성 성공 시 HTTP 201 Created 응답과 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body("공지사항이 성공적으로 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 (로그인 필요) HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            // 관리자 권한이 없는 경우 HTTP 403 Forbidden 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // 공지사항 생성 중 기타 예외 발생 시 HTTP 500 Internal Server Error 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 생성 실패: " + e.getMessage());
        }
    }

    /**
     * 기존 공지사항을 수정합니다.
     * 이 API는 관리자만 접근할 수 있으며, 해당 공지사항의 작성자(관리자)만 수정 가능합니다.
     *
     * @param id 수정할 공지사항의 고유 ID (경로 변수).
     * @param noticeDTO 수정된 공지사항 정보를 담고 있는 NoticeDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 공지사항 수정 성공 또는 실패 메시지와 함께 HTTP 응답을 반환합니다.
     */
    @PutMapping("/{id}") // PUT 요청을 "/api/notices/{id}" 경로로 매핑합니다.
    public ResponseEntity<String> updateNotice(@PathVariable("id") int id, @RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        try {
            // 요청에서 관리자 사용자 ID를 추출하고 권한을 확인합니다.
            int adminUserId = getAdminUserIdFromRequest(request);

            // 수정하려는 공지사항의 기존 정보를 가져옵니다.
            NoticeDTO existingNotice = noticeService.getNoticeDetail(id);
            if (existingNotice == null) {
                // 공지사항을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항을 찾을 수 없습니다.");
            }
            // 기존 공지사항의 작성자(userId)가 현재 로그인된 관리자 ID와 일치하는지 확인합니다.
            // DTO의 ID 필드가 noticeId에서 id로 변경되었으므로, getter도 getId()로 변경됩니다.
            if (existingNotice.getUserId() != null && existingNotice.getUserId() != adminUserId) {
                throw new SecurityException("공지사항 수정 권한이 없습니다: 작성자 불일치.");
            }

            // DTO에 경로 변수에서 받은 ID를 설정합니다. (NoticeDTO의 setNoticeId -> setId)
            noticeDTO.setId(id);
            // 기존 공지사항의 작성자 ID를 유지합니다. (요청 DTO에 포함되지 않을 수 있으므로)
            noticeDTO.setUserId(existingNotice.getUserId());

            // NoticeService를 통해 공지사항을 수정합니다.
            noticeService.updateNotice(noticeDTO);
            // 공지사항 수정 성공 시 HTTP 200 OK 응답과 메시지를 반환합니다.
            return ResponseEntity.ok("공지사항이 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            // 관리자 권한이 없거나 작성자 불일치 등의 보안 예외 발생 시 HTTP 403 Forbidden 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // 공지사항 수정 중 기타 예외 발생 시 HTTP 500 Internal Server Error 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 수정 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 공지사항을 삭제합니다.
     * 이 API는 관리자만 접근할 수 있으며, 해당 공지사항의 작성자(관리자)만 삭제 가능합니다.
     *
     * @param id 삭제할 공지사항의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 공지사항 삭제 성공 또는 실패 메시지와 함께 HTTP 응답을 반환합니다.
     */
    @DeleteMapping("/{id}") // DELETE 요청을 "/api/notices/{id}" 경로로 매핑합니다.
    public ResponseEntity<String> deleteNotice(@PathVariable("id") int id, HttpServletRequest request) {
        try {
            // 요청에서 관리자 사용자 ID를 추출하고 권한을 확인합니다.
            int adminUserId = getAdminUserIdFromRequest(request);

            // 삭제하려는 공지사항의 기존 정보를 가져옵니다.
            NoticeDTO existingNotice = noticeService.getNoticeDetail(id);
            if (existingNotice == null) {
                // 공지사항을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항을 찾을 수 없습니다.");
            }
            // 기존 공지사항의 작성자(userId)가 현재 로그인된 관리자 ID와 일치하는지 확인합니다.
            // DTO의 ID 필드가 noticeId에서 id로 변경되었으므로, getter도 getId()로 변경됩니다.
            if (existingNotice.getUserId() != null && existingNotice.getUserId() != adminUserId) {
                throw new SecurityException("공지사항 삭제 권한이 없습니다: 작성자 불일치.");
            }

            // NoticeService를 통해 공지사항을 삭제합니다.
            noticeService.deleteNotice(id);
            // 공지사항 삭제 성공 시 HTTP 200 OK 응답과 메시지를 반환합니다.
            return ResponseEntity.ok("공지사항이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 없거나 유효하지 않은 경우 HTTP 401 Unauthorized 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            // 관리자 권한이 없거나 작성자 불일치 등의 보안 예외 발생 시 HTTP 403 Forbidden 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // 공지사항 삭제 중 기타 예외 발생 시 HTTP 500 Internal Server Error 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공지사항 삭제 실패: " + e.getMessage());
        }
    }
}