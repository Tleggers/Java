package com.Trekkit_Java.Controller;

import com.Trekkit_Java.DTO.PostDTO;
import com.Trekkit_Java.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import com.Trekkit_Java.Util.JwtUtil; // JwtUtil 사용을 위해 추가 (기존에 있다면 그대로 두세요)
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final JwtUtil jwtUtil; // JwtUtil 주입

    @Value("${jwt.secret}")
    private String jwtSecret;

    // --- 추가: JWT에서 userId 추출하는 헬퍼 메서드 ---
    private Long getUserIdFromJwtToken(HttpServletRequest request) {
        String token = null;
        try {
            // 1. Authorization 헤더에서 토큰 추출 (Bearer ...)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                System.out.println("JWT Debug: Token from Authorization header: " + token); // 디버그 로그
            }

            // 2. 쿠키에서 토큰 추출
            if (token == null && request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) { // 실제 JWT 쿠키 이름으로 변경 필요
                        String rawCookieValue = cookie.getValue();
                        System.out.println("JWT Debug: Raw Cookie Value: " + rawCookieValue); // 디버그 로그

                        // 'jwte' 접두사 제거 (CommunityDetail에서 확인된 문제)
                        if (rawCookieValue.startsWith("jwte")) {
                            token = rawCookieValue.substring("jwte".length());
                            System.out.println("JWT Debug: Token after stripping prefix: " + token); // 디버그 로그
                        } else {
                            token = rawCookieValue;
                        }
                        break;
                    }
                }
            }

            if (token == null || token.isEmpty()) {
                System.err.println("PostController: JWT Debug: Token is null or empty. Returning null userId.");
                return null; // 토큰 없음
            }

            // JWT 파싱 및 Claims 추출
            // setSigningKey에 사용할 키는 바이트 배열이어야 합니다.
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(jwtSecret.getBytes()) // String -> byte[]로 변환
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

            Long userId = claims.get("id", Long.class); // JWT 페이로드에 'id'가 Long 타입으로 있다고 가정
            System.out.println("PostController: JWT Debug: Successfully extracted userId: " + userId + " from token."); // 디버그 로그
            return userId;

        } catch (Exception e) {
            System.err.println("PostController: JWT 토큰 파싱 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스 출력
            System.err.println("PostController: JWT Debug: Failed to extract userId. Returning null userId.");
            return null; // 유효하지 않은 토큰
        }
    }
    // --- 헬퍼 메서드 끝 ---

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "최신순") String sort,
            @RequestParam(name = "mountain", required = false) String mountain) {

        Map<String, Object> response = postService.getAllPosts(page, size, sort, mountain);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable("postId") Long postId) {
        PostDTO post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping // createPost 메서드
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO, HttpServletRequest request) {
        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출
        System.out.println("PostController: createPost Debug: Extracted userId from token: " + userId); // 디버그 로그

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            PostDTO createdPost = postService.createPost(postDTO, userId); // userId를 PostService로 전달
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 작성 중 서버 오류 발생");
        }
    }

    @PutMapping("/{postId}") // updatePost 메서드 수정
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId,
                                     @RequestBody PostDTO postDTO,
                                     HttpServletRequest request) { // @AuthenticationPrincipal 제거, HttpServletRequest 추가
        postDTO.setId(postId);
        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            PostDTO updatedPost = postService.updatePost(postDTO, userId);
            return ResponseEntity.ok(updatedPost);
        } catch (SecurityException e) {
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) { // 기타 예외 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 서버 오류 발생");
        }
    }

    @DeleteMapping("/{postId}") // deletePost 메서드 수정
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, HttpServletRequest request) { // @AuthenticationPrincipal 제거, HttpServletRequest 추가
        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) { // 기타 예외 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 서버 오류 발생");
        }
    }

    @PostMapping("/{postId}/like") // toggleLike 메서드 수정
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("postId") Long postId,
                                                       HttpServletRequest request) { // @AuthenticationPrincipal 제거, HttpServletRequest 추가
        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }
        boolean isLiked = postService.toggleLike(postId, userId);
        int likeCount = postService.getLikeCount(postId);
        return ResponseEntity.ok(Map.of("isLiked", isLiked, "likeCount", likeCount));
    }

    @PostMapping("/{postId}/bookmark") // toggleBookmark 메서드 수정
    public ResponseEntity<Map<String, Boolean>> toggleBookmark(@PathVariable("postId") Long postId,
                                                              HttpServletRequest request) { // @AuthenticationPrincipal 제거, HttpServletRequest 추가
        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isBookmarked = postService.toggleBookmark(postId, userId);
        return ResponseEntity.ok(Map.of("isBookmarked", isBookmarked));
    }
}