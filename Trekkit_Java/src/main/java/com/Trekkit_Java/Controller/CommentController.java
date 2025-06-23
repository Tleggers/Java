package com.Trekkit_Java.Controller;

import java.util.List; // List 인터페이스 사용을 위한 임포트
import org.springframework.http.HttpStatus; // HTTP 상태 코드 사용을 위한 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답을 위한 클래스 임포트
import org.springframework.web.bind.annotation.*; // Spring Web 관련 어노테이션들 임포트
import com.Trekkit_Java.DTO.CommentDTO; // 댓글 데이터 전송 객체(DTO) 임포트
import com.Trekkit_Java.Service.CommentService; // 댓글 관련 비즈니스 로직을 처리하는 서비스 임포트
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor 어노테이션 임포트 (final 필드를 사용하는 생성자를 자동으로 생성)
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 정보를 접근하기 위한 임포트
import io.jsonwebtoken.Claims; // JWT 클레임(페이로드)에 접근하기 위한 임포트
import io.jsonwebtoken.Jwts; // JWT 파싱 및 생성 유틸리티 임포트
import org.springframework.beans.factory.annotation.Value; // Spring의 @Value 어노테이션 임포트 (속성 값 주입)

/**
 * 댓글 관련 API 요청을 처리하는 Spring REST 컨트롤러입니다.
 * 모든 요청은 "/api/comments" 경로로 매핑됩니다.
 * JWT 토큰을 통해 사용자 인증 및 권한을 확인합니다.
 */
@RestController // 이 클래스가 RESTful 웹 서비스의 컨트롤러임을 나타냅니다.
@RequestMapping("/api/comments") // 이 컨트롤러의 모든 핸들러 메서드에 대한 기본 경로를 설정합니다.
@RequiredArgsConstructor // Lombok 어노테이션: final로 선언된 필드(commentService)를 초기화하는 생성자를 자동으로 생성합니다.
public class CommentController {

    private final CommentService commentService; // CommentService를 주입받아 댓글 관련 비즈니스 로직을 수행합니다.

    // application.properties 또는 application.yml에서 'jwt.secret' 속성 값을 주입받습니다.
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * HTTP 요청(HttpServletRequest)에서 JWT 토큰을 추출하고 파싱하여 사용자 ID를 반환합니다.
     * 토큰은 "Authorization" 헤더(Bearer 스키마) 또는 "jwt" 쿠키에서 찾습니다.
     *
     * @param request 현재 HTTP 요청 객체.
     * @return 파싱된 사용자 ID (Long 타입). 토큰이 없거나 유효하지 않으면 null을 반환합니다.
     */
    private Long getUserIdFromJwtToken(HttpServletRequest request) {
        try {
            String token = null;
            // 1. "Authorization" 헤더에서 토큰 추출 시도 (예: "Bearer eyJ...")
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // "Bearer " 접두사(7글자) 이후의 문자열이 실제 토큰입니다.
            }

            // 2. "Authorization" 헤더에 토큰이 없으면 쿠키에서 토큰 추출 시도
            if (token == null && request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) { // 쿠키의 이름이 'jwt'인지 확인합니다.
                        String rawCookieValue = cookie.getValue();
                        // 클라이언트(예: Flutter 웹)에서 "jwte"와 같은 접두사를 붙여 보낼 경우 이를 제거합니다.
                        if (rawCookieValue.startsWith("jwte")) {
                            token = rawCookieValue.substring("jwte".length()); // "jwte" 4글자 제거
                        } else {
                            token = rawCookieValue; // 접두사가 없으면 쿠키 값을 그대로 사용합니다.
                        }
                        break; // 'jwt' 쿠키를 찾으면 루프를 종료합니다.
                    }
                }
            }

            // 토큰이 여전히 null이거나 비어있으면 사용자 ID를 반환할 수 없으므로 null을 반환합니다.
            if (token == null || token.isEmpty()) {
                System.err.println("Backend JWT Debug: 토큰이 null이거나 비어있습니다. userId를 반환하지 않습니다.");
                return null;
            }
            System.out.println("Backend JWT Debug: 수신된 토큰: " + token); // 디버깅을 위해 수신된 토큰을 출력합니다.

            // JWT 파싱 및 클레임(payload) 추출
            // `jwtSecret`를 바이트 배열로 변환하여 서명 키로 설정하고 토큰을 파싱합니다.
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(jwtSecret.getBytes()) // JWT 서명에 사용된 시크릿 키를 설정합니다.
                                .build()
                                .parseClaimsJws(token) // JWS(JSON Web Signature) 토큰을 파싱합니다.
                                .getBody(); // 토큰의 페이로드(클레임)를 가져옵니다.

            // 클레임에서 'id' 필드를 Long 타입으로 추출합니다. JWT 페이로드에 사용자 ID가 'id'라는 키로 저장되어 있다고 가정합니다.
            Long userId = claims.get("id", Long.class);
            System.out.println("Backend JWT Debug: 파싱된 userId: " + userId + ", 모든 클레임: " + claims.toString()); // 파싱된 userId와 모든 클레임을 출력합니다.
            return userId; // 추출된 사용자 ID를 반환합니다.

        } catch (Exception e) {
            // 토큰 파싱 또는 처리 중 예외 발생 시 오류 메시지를 출력하고 null을 반환합니다.
            System.err.println("Backend JWT Error: 토큰 파싱 실패: " + e.getMessage());
            e.printStackTrace(); // 예외 스택 트레이스를 출력하여 상세 오류 정보를 확인합니다.
            return null; // 유효하지 않은 토큰 처리
        }
    }

    /**
     * 특정 게시글의 댓글 목록을 조회합니다.
     *
     * @param postId 댓글을 조회할 게시글의 고유 ID.
     * @return 조회된 댓글 목록(CommentDTO 리스트)과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @GetMapping("/post/{postId}") // GET 요청을 "/api/comments/post/{postId}" 경로로 매핑합니다.
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId); // CommentService를 통해 댓글 목록을 가져옵니다.
        return ResponseEntity.ok(comments); // HTTP 200 OK 상태 코드와 함께 댓글 목록을 반환합니다.
    }

    /**
     * 새로운 댓글을 생성합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 댓글 작성자를 확인하고 인증되지 않은 요청을 거부합니다.
     *
     * @param comment 댓글 정보를 담고 있는 CommentDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 생성된 댓글 정보(CommentDTO) 또는 오류 메시지와 함께 HTTP 응답을 반환합니다.
     */
    @PostMapping // POST 요청을 "/api/comments" 경로로 매핑합니다.
    public ResponseEntity<?> createComment(
            @RequestBody CommentDTO comment, // 요청 본문의 JSON 데이터를 CommentDTO 객체로 바인딩합니다.
            HttpServletRequest request) { // JWT 토큰을 추출하기 위해 HttpServletRequest를 주입받습니다.

        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.

        // 사용자 ID가 null이면 (즉, 토큰이 없거나 유효하지 않으면) 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            CommentDTO createdComment = commentService.createComment(comment, userId); // CommentService를 통해 댓글을 생성합니다.
            return ResponseEntity.ok(createdComment); // 생성된 댓글 정보와 함께 HTTP 200 OK 응답을 반환합니다.
        } catch (Exception e) {
            // 댓글 작성 중 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 기존 댓글을 수정합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 댓글 수정 권한을 확인합니다.
     *
     * @param commentId 수정할 댓글의 고유 ID (경로 변수).
     * @param comment 수정된 댓글 정보를 담고 있는 CommentDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 수정된 댓글 정보(CommentDTO) 또는 오류 메시지와 함께 HTTP 응답을 반환합니다.
     */
    @PutMapping("/{commentId}") // PUT 요청을 "/api/comments/{commentId}" 경로로 매핑합니다.
    public ResponseEntity<?> updateComment(
            @PathVariable("commentId") Long commentId, // URL 경로에서 commentId를 추출합니다.
            @RequestBody CommentDTO comment, // 요청 본문의 JSON 데이터를 CommentDTO 객체로 바인딩합니다.
            HttpServletRequest request) { // JWT 토큰을 추출하기 위해 HttpServletRequest를 주입받습니다.

        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.

        // 사용자 ID가 null이면 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            comment.setId(commentId); // 경로 변수에서 받은 commentId를 CommentDTO에 설정합니다.
            CommentDTO updatedComment = commentService.updateComment(comment, userId); // CommentService를 통해 댓글을 수정합니다.
            return ResponseEntity.ok(updatedComment); // 수정된 댓글 정보와 함께 HTTP 200 OK 응답을 반환합니다.
        } catch (SecurityException e) {
            // 권한 없음 (SecurityException) 발생 시 HTTP 403 Forbidden 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // 댓글 수정 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 특정 댓글을 삭제합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 댓글 삭제 권한을 확인합니다.
     *
     * @param commentId 삭제할 댓글의 고유 ID (경로 변수).
     * @param postId 댓글이 속한 게시글의 고유 ID (경로 변수, 백엔드 로직에 따라 필요할 수 있음).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 성공 시 HTTP 200 OK (응답 본문 없음) 또는 오류 메시지와 함께 HTTP 응답을 반환합니다.
     */
    @DeleteMapping("/{commentId}/post/{postId}") // DELETE 요청을 "/api/comments/{commentId}/post/{postId}" 경로로 매핑합니다.
    public ResponseEntity<?> deleteComment(
            @PathVariable("commentId") Long commentId, // URL 경로에서 commentId를 추출합니다.
            @PathVariable("postId") Long postId, // URL 경로에서 postId를 추출합니다.
            HttpServletRequest request) { // JWT 토큰을 추출하기 위해 HttpServletRequest를 주입받습니다.

        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.

        // 사용자 ID가 null이면 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            commentService.deleteComment(commentId, postId, userId); // CommentService를 통해 댓글을 삭제합니다.
            return ResponseEntity.ok().build(); // 성공적으로 삭제되면 HTTP 200 OK 응답을 반환하고 본문은 비웁니다.
        } catch (SecurityException e) {
            // 권한 없음 (SecurityException) 발생 시 HTTP 403 Forbidden 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // 댓글 삭제 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}