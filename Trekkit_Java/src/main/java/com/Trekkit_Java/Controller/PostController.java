package com.Trekkit_Java.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value; // @Value 사용을 위해 추가
// --- 추가 import 끝 ---
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Trekkit_Java.DTO.PostDTO;
import com.Trekkit_Java.Service.PostService;
import com.Trekkit_Java.Util.JwtUtil; // JwtUtil 사용을 위해 추가

import io.jsonwebtoken.Claims; // JWT 라이브러리에 따라 다를 수 있음
import io.jsonwebtoken.Jwts; // JWT 라이브러리에 따라 다를 수 있음
// --- 추가 import ---
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final JwtUtil jwtUtil; // JwtUtil 주입

    @Value("${jwt.secret}")
    private String jwtSecret;

    // --- JWT에서 userId 추출하는 헬퍼 메서드 (재추가) ---
    private Long getUserIdFromJwtToken(HttpServletRequest request) {
        String token = null;
        try {
            // 1. Authorization 헤더에서 토큰 추출 (Bearer ...)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                System.out.println("JWT Debug: Token from Authorization header: " + token);
            }

            // 2. 쿠키에서 토큰 추출
            if (token == null && request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) { // 실제 JWT 쿠키 이름 확인 (application.properties 참조)
                        String rawCookieValue = cookie.getValue();
                        System.out.println("JWT Debug: Raw Cookie Value from cookie: " + rawCookieValue);
                        // 'jwte' 접두사 제거 (프론트엔드에서 쿠키에 이렇게 저장되었다면)
                        if (rawCookieValue.startsWith("jwte")) { // 이 부분은 `jwteyJ` 접두사가 있다면 'jwte' 대신 'jwteyJ' 로 변경해야 할 수도 있습니다.
                            token = rawCookieValue.substring("jwte".length()); // `jwte` 길이에 맞게 자름
                            System.out.println("JWT Debug: Token after stripping prefix: " + token);
                        } else {
                            token = rawCookieValue;
                        }
                        break;
                    }
                }
            }

            if (token == null || token.isEmpty()) {
                System.err.println("PostController: JWT Debug: Token is null or empty. Returning null userId.");
                return null;
            }

            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(jwtSecret.getBytes())
                                .build()
                                .parseClaimsJws(token)
                                .getBody();

            Long userId = claims.get("id", Long.class); // JWT 페이로드에 'id'가 Long 타입으로 있다고 가정
            System.out.println("PostController: JWT Debug: Successfully extracted userId: " + userId + " from token.");
            return userId;

        } catch (Exception e) {
            System.err.println("PostController: JWT 토큰 파싱 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            System.err.println("PostController: JWT Debug: Failed to extract userId. Returning null userId.");
            return null;
        }
    }
    // --- 헬퍼 메서드 끝 ---

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "최신순") String sort,
            @RequestParam(name = "mountain", required = false) String mountain,
            HttpServletRequest request) { // JWT 디버깅을 위해 HttpServletRequest 추가 (옵션)

        // 이 메서드는 일반적으로 인증이 필요 없지만, JWT 디버깅을 위해 request를 추가할 수 있습니다.
        // Long debugUserId = getUserIdFromJwtToken(request); // 디버깅 목적
        // System.out.println("PostController: getPosts Debug: Current userId (if authenticated): " + debugUserId);

        Map<String, Object> response = postService.getAllPosts(page, size, sort, mountain);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable("postId") Long postId, HttpServletRequest request) { // HttpServletRequest 추가 (옵션)
        // Long debugUserId = getUserIdFromJwtToken(request); // 디버깅 목적
        // System.out.println("PostController: getPost Debug: Current userId (if authenticated): " + debugUserId);

        PostDTO post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping // createPost 메서드 수정
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO, HttpServletRequest request) { // HttpServletRequest로 변경
        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출
        System.out.println("PostController: createPost Debug: Extracted userId from token: " + userId);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            PostDTO createdPost = postService.createPost(postDTO, userId);
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 작성 중 서버 오류 발생");
        }
    }
    
    @PostMapping("/upload-images") // URL 경로: /api/posts/upload-images
    public ResponseEntity<?> uploadImages(@RequestParam("images") MultipartFile[] files,
                                         HttpServletRequest request) { // HttpServletRequest를 통해 userId 검증

        Long userId = getUserIdFromJwtToken(request); // JWT에서 userId 추출
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            List<String> uploadedPaths = postService.uploadPostImages(files); // PostService에 이 메서드 추가 필요
            return ResponseEntity.ok(Map.of("imagePaths", uploadedPaths));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "이미지 업로드 중 오류 발생: " + e.getMessage()));
        }
    }

    @PutMapping("/{postId}") // updatePost 메서드 수정
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId,
                                     @RequestBody PostDTO postDTO,
                                     HttpServletRequest request) { // HttpServletRequest로 변경
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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 서버 오류 발생");
        }
    }

    @DeleteMapping("/{postId}") // deletePost 메서드 수정
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, HttpServletRequest request) { // HttpServletRequest로 변경
        Long userId = getUserIdFromJwtToken(request);

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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 서버 오류 발생");
        }
    }

    @PostMapping("/{postId}/like") // toggleLike 메서드 수정
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("postId") Long postId,
                                                       HttpServletRequest request) { // HttpServletRequest로 변경
        Long userId = getUserIdFromJwtToken(request);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }
        try {
            boolean isLiked = postService.toggleLike(postId, userId);
            int likeCount = postService.getLikeCount(postId);
            return ResponseEntity.ok(Map.of("isLiked", isLiked, "likeCount", likeCount));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "좋아요 처리 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }

    @PostMapping("/{postId}/bookmark") // toggleBookmark 메서드 수정
    public ResponseEntity<Map<String, Boolean>> toggleBookmark(@PathVariable("postId") Long postId,
                                                              HttpServletRequest request) { // HttpServletRequest로 변경
        Long userId = getUserIdFromJwtToken(request);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            boolean isBookmarked = postService.toggleBookmark(postId, userId);
            return ResponseEntity.ok(Map.of("isBookmarked", isBookmarked));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Boolean> errorMap = new HashMap<>();
            errorMap.put("isBookmarked", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }

    @GetMapping("/mountains") // URL 경로: /api/posts/mountains
    public ResponseEntity<List<String>> getMountainNames(
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            HttpServletRequest request) { // HttpServletRequest 추가 (옵션)
        // Long debugUserId = getUserIdFromJwtToken(request); // 디버깅 목적
        // System.out.println("PostController: getMountainNames Debug: Current userId (if authenticated): " + debugUserId);

        if (clientType == null || !clientType.equals("web")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> mountainNames = postService.getAllMountainNames();
        if (mountainNames.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mountainNames);
    }
}