package com.Trekkit_Java.Controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Trekkit_Java.DTO.CommentDTO;
import com.Trekkit_Java.Service.CommentService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value; // @Value 사용을 위해 추가

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Value("${jwt.secret}") // application.properties 또는 application.yml에서 JWT 시크릿 키 로드
    private String jwtSecret;

    // JWT 파싱 함수
    private Long getUserIdFromJwtToken(HttpServletRequest request) {
        try {
            String token = null;
            // 1. Authorization 헤더에서 토큰 추출 (Bearer ...)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // 2. 쿠키에서 토큰 추출
            if (token == null && request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) { // 예시: 쿠키 이름이 'jwt'라고 가정
                        String rawCookieValue = cookie.getValue();
                        if (rawCookieValue.startsWith("jwte")) {
                            token = rawCookieValue.substring("jwte".length()); // "jwte" 4글자 제거
                        } else {
                            token = rawCookieValue; // 접두사가 없으면 그대로 사용
                        }
                        break;
                    }
                }
            }

            if (token == null || token.isEmpty()) {
                System.err.println("Backend JWT Debug: Token is null or empty. Returning null userId.");
                return null; // 토큰 없음
            }
            System.out.println("Backend JWT Debug: Received token: " + token); // 백엔드가 받은 토큰 값

            // JWT 파싱 및 Claims 추출 (중복 코드 제거)
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(jwtSecret.getBytes()) // String -> byte[]로 변환
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

            // 클레임에서 'id' 추출. JWT payload에 'id' 필드가 Long 타입으로 있다고 가정합니다.
            Long userId = claims.get("id", Long.class);
            System.out.println("Backend JWT Debug: Parsed userId: " + userId + ", Claims: " + claims.toString()); // 파싱된 userId 및 모든 클레임
            return userId;

        } catch (Exception e) {
            System.err.println("Backend JWT Error: Failed to parse token: " + e.getMessage()); // 오류 메시지 출력
            e.printStackTrace(); // 스택 트레이스 출력하여 더 자세한 정보 확인
            return null; // 유효하지 않은 토큰
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody CommentDTO comment,
            HttpServletRequest request) {

        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            CommentDTO createdComment = commentService.createComment(comment, userId);
            return ResponseEntity.ok(createdComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 중 오류 발생: " + e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentDTO comment,
            HttpServletRequest request) {

        Long userId = getUserIdFromJwtToken(request);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            comment.setId(commentId);
            CommentDTO updatedComment = commentService.updateComment(comment, userId);
            return ResponseEntity.ok(updatedComment);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류 발생: " + e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}/post/{postId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("commentId") Long commentId,
            @PathVariable("postId") Long postId,
            HttpServletRequest request) {

        Long userId = getUserIdFromJwtToken(request);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            commentService.deleteComment(commentId, postId, userId);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}