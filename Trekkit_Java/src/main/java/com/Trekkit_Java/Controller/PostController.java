package com.Trekkit_Java.Controller;

import java.util.HashMap; // HashMap 사용을 위한 임포트
import java.util.List; // List 인터페이스 사용을 위한 임포트
import java.util.Map; // Map 인터페이스 사용을 위한 임포트

import org.springframework.beans.factory.annotation.Value; // Spring의 @Value 어노테이션 임포트 (속성 값 주입)
import org.springframework.http.HttpStatus; // HTTP 상태 코드 사용을 위한 임포트
import org.springframework.http.ResponseEntity; // HTTP 응답을 위한 클래스 임포트
import org.springframework.web.bind.annotation.DeleteMapping; // DELETE HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.GetMapping; // GET HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.PathVariable; // URL 경로 변수 추출 어노테이션
import org.springframework.web.bind.annotation.PostMapping; // POST HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.PutMapping; // PUT HTTP 메서드 매핑 어노테이션
import org.springframework.web.bind.annotation.RequestBody; // 요청 본문 매핑 어노테이션
import org.springframework.web.bind.annotation.RequestHeader; // 요청 헤더 추출 어노테이션
import org.springframework.web.bind.annotation.RequestMapping; // 요청 매핑 어노테이션
import org.springframework.web.bind.annotation.RequestParam; // 쿼리 파라미터 추출 어노테이션
import org.springframework.web.bind.annotation.RestController; // REST 컨트롤러 어노테이션
import org.springframework.web.multipart.MultipartFile; // 파일 업로드를 위한 MultipartFile 임포트

import com.Trekkit_Java.DTO.PostDTO; // 게시글 데이터 전송 객체(DTO) 임포트
import com.Trekkit_Java.Service.PostService; // 게시글 관련 비즈니스 로직 서비스 임포트
import com.Trekkit_Java.Util.JwtUtil; // JWT 유틸리티 (토큰 파싱 등) 임포트

import io.jsonwebtoken.Claims; // JWT 클레임(페이로드) 접근
import io.jsonwebtoken.Jwts; // JWT 파싱 및 생성 유틸리티
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 정보 접근을 위한 임포트
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor 어노테이션

/**
 * 게시글 관련 API 요청을 처리하는 Spring REST 컨트롤러입니다.
 * 모든 요청은 "/api/posts" 경로로 매핑됩니다.
 * JWT 토큰을 통해 사용자 인증 및 권한을 확인하고, 이미지 업로드 등의 기능을 제공합니다.
 */
@RestController // 이 클래스가 RESTful 웹 서비스의 컨트롤러임을 나타냅니다.
@RequestMapping("/api/posts") // 이 컨트롤러의 모든 핸들러 메서드에 대한 기본 경로를 설정합니다.
@RequiredArgsConstructor // Lombok 어노테이션: final 필드(postService, jwtUtil)를 주입하는 생성자를 자동으로 생성합니다.
public class PostController {
    private final PostService postService; // PostService를 주입받아 게시글 관련 비즈니스 로직을 수행합니다.
    private final JwtUtil jwtUtil; // JwtUtil을 주입받아 JWT 관련 유틸리티 기능을 사용합니다.

    @Value("${jwt.secret}") // 'jwt.secret' 속성 값을 application.properties 또는 application.yml에서 주입받습니다.
    private String jwtSecret;

    /**
     * HTTP 요청(HttpServletRequest)에서 JWT 토큰을 추출하고 파싱하여 사용자 ID(Long 타입)를 반환하는 헬퍼 메서드입니다.
     * 토큰은 "Authorization" 헤더(Bearer 스키마) 또는 "jwt" 쿠키에서 찾습니다.
     *
     * @param request 현재 HTTP 요청 객체.
     * @return 파싱된 사용자 ID (Long 타입). 토큰이 없거나 유효하지 않으면 null을 반환합니다.
     */
    private Long getUserIdFromJwtToken(HttpServletRequest request) {
        String token = null;
        try {
            // 1. "Authorization" 헤더에서 토큰 추출 시도 (예: "Bearer eyJ...")
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7); // "Bearer " 접두사(7글자) 이후의 문자열이 실제 토큰입니다.
                System.out.println("JWT Debug: Authorization 헤더에서 추출된 토큰: " + token);
            }

            // 2. "Authorization" 헤더에 토큰이 없으면 쿠키에서 토큰 추출 시도
            if (token == null && request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) { // 쿠키의 이름이 'jwt'인지 확인합니다.
                        String rawCookieValue = cookie.getValue();
                        System.out.println("JWT Debug: 쿠키에서 추출된 원본 값: " + rawCookieValue);
                        // 클라이언트(예: Flutter 웹)에서 특정 접두사를 붙여 보낼 경우 이를 제거합니다.
                        if (rawCookieValue.startsWith("jwte")) { // 이 부분은 실제 사용되는 접두사에 따라 변경해야 할 수 있습니다.
                            token = rawCookieValue.substring("jwte".length()); // `jwte` 길이만큼 잘라 접두사를 제거합니다.
                            System.out.println("JWT Debug: 접두사 제거 후 토큰: " + token);
                        } else {
                            token = rawCookieValue; // 접두사가 없으면 쿠키 값을 그대로 사용합니다.
                        }
                        break; // 'jwt' 쿠키를 찾으면 루프를 종료합니다.
                    }
                }
            }

            // 토큰이 여전히 null이거나 비어있으면 사용자 ID를 반환할 수 없으므로 null을 반환합니다.
            if (token == null || token.isEmpty()) {
                System.err.println("PostController: JWT Debug: 토큰이 null이거나 비어있습니다. userId를 반환하지 않습니다.");
                return null;
            }

            // JWT 파싱 및 클레임(payload) 추출
            // `jwtSecret`를 바이트 배열로 변환하여 서명 키로 설정하고 토큰을 파싱합니다.
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(jwtSecret.getBytes()) // JWT 서명에 사용된 시크릿 키를 설정합니다.
                                .build()
                                .parseClaimsJws(token) // JWS(JSON Web Signature) 토큰을 파싱합니다.
                                .getBody(); // 토큰의 페이로드(클레임)를 가져옵니다.

            // 클레임에서 'id' 필드를 Long 타입으로 추출합니다. JWT 페이로드에 사용자 ID가 'id'라는 키로 저장되어 있다고 가정합니다.
            Long userId = claims.get("id", Long.class);
            System.out.println("PostController: JWT Debug: 토큰에서 userId 성공적으로 추출: " + userId);
            return userId; // 추출된 사용자 ID를 반환합니다.

        } catch (Exception e) {
            // 토큰 파싱 또는 처리 중 예외 발생 시 오류 메시지를 출력하고 null을 반환합니다.
            System.err.println("PostController: JWT 토큰 파싱 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 예외 스택 트레이스를 출력하여 상세 오류 정보를 확인합니다.
            System.err.println("PostController: JWT Debug: userId 추출 실패. null userId를 반환합니다.");
            return null; // 유효하지 않은 토큰 처리
        }
    }

    /**
     * 게시글 목록을 조회합니다.
     * 정렬 기준, 산 필터, 페이지 번호, 페이지당 개수를 쿼리 파라미터로 받아 처리합니다.
     * 이 메서드는 일반적으로 인증이 필요하지 않습니다.
     *
     * @param page 조회할 페이지 번호 (기본값: 0).
     * @param size 페이지당 게시글 개수 (기본값: 10).
     * @param sort 게시글 정렬 기준 (예: "최신순", "인기순").
     * @param mountain 특정 산으로 필터링할 경우의 산 이름 (선택 사항).
     * @param request (옵션) JWT 디버깅을 위해 HttpServletRequest를 주입받을 수 있습니다.
     * @return 게시글 목록과 전체 개수(`totalCount`)를 포함하는 Map과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @GetMapping // GET 요청을 "/api/posts" 경로로 매핑합니다.
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(name = "page", defaultValue = "0") int page, // 'page' 쿼리 파라미터, 기본값 0
            @RequestParam(name = "size", defaultValue = "10") int size, // 'size' 쿼리 파라미터, 기본값 10
            @RequestParam(name = "sort", defaultValue = "최신순") String sort, // 'sort' 쿼리 파라미터, 기본값 "최신순"
            @RequestParam(name = "mountain", required = false) String mountain, // 'mountain' 쿼리 파라미터 (선택 사항)
            HttpServletRequest request) { // HttpServletRequest를 주입받아 JWT 디버깅에 사용할 수 있습니다.

        // Long debugUserId = getUserIdFromJwtToken(request); // 디버깅 목적으로 주석 해제하여 현재 로그인된 사용자 ID를 확인할 수 있습니다.
        // System.out.println("PostController: getPosts Debug: 현재 userId (인증된 경우): " + debugUserId);

        // PostService를 통해 필터링 및 정렬된 게시글 목록을 가져옵니다.
        Map<String, Object> response = postService.getAllPosts(page, size, sort, mountain);
        return ResponseEntity.ok(response); // HTTP 200 OK 상태 코드와 함께 응답 데이터를 반환합니다.
    }

    /**
     * 특정 게시글의 상세 정보를 조회합니다.
     *
     * @param postId 조회할 게시글의 고유 ID (경로 변수).
     * @param request (옵션) JWT 디버깅을 위해 HttpServletRequest를 주입받을 수 있습니다.
     * @return 조회된 게시글 정보(PostDTO) 또는 HTTP 404 Not Found 응답을 반환합니다.
     */
    @GetMapping("/{postId}") // GET 요청을 "/api/posts/{postId}" 경로로 매핑합니다.
    public ResponseEntity<PostDTO> getPost(@PathVariable("postId") Long postId, HttpServletRequest request) { // HttpServletRequest 주입 (옵션)
        // Long debugUserId = getUserIdFromJwtToken(request); // 디버깅 목적으로 주석 해제하여 현재 로그인된 사용자 ID를 확인할 수 있습니다.
        // System.out.println("PostController: getPost Debug: 현재 userId (인증된 경우): " + debugUserId);

        PostDTO post = postService.getPostById(postId); // PostService를 통해 게시글 상세 정보를 가져옵니다.
        if (post == null) {
            // 게시글을 찾을 수 없으면 HTTP 404 Not Found 응답을 반환합니다.
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post); // HTTP 200 OK 상태 코드와 함께 게시글 정보를 반환합니다.
    }

    /**
     * 새 게시글을 생성합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 게시글 작성자를 확인하고 인증되지 않은 요청을 거부합니다.
     *
     * @param postDTO 게시글 정보를 담고 있는 PostDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 생성된 게시글 정보(PostDTO)와 함께 HTTP 201 Created 응답을 반환합니다.
     */
    @PostMapping // POST 요청을 "/api/posts" 경로로 매핑합니다.
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO, HttpServletRequest request) {
        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.
        System.out.println("PostController: createPost Debug: 토큰에서 추출된 userId: " + userId);

        // 사용자 ID가 null이면 (즉, 토큰이 없거나 유효하지 않으면) 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // PostService를 통해 게시글을 생성하고, 생성된 게시글 정보를 반환받습니다.
            PostDTO createdPost = postService.createPost(postDTO, userId);
            // HTTP 201 Created 상태 코드와 함께 생성된 게시글 정보를 반환합니다.
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
            // 게시글 작성 중 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 작성 중 서버 오류 발생");
        }
    }
    
    /**
     * 게시글에 첨부할 이미지를 업로드합니다.
     * 이 API는 인증이 필요하며, JWT 토큰으로 사용자 ID를 확인합니다.
     *
     * @param files 업로드할 MultipartFile 배열.
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 업로드된 이미지 파일의 경로 리스트를 포함하는 Map과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/upload-images") // POST 요청을 "/api/posts/upload-images" 경로로 매핑합니다.
    public ResponseEntity<?> uploadImages(@RequestParam("images") MultipartFile[] files,
                                         HttpServletRequest request) { // HttpServletRequest를 통해 userId를 검증합니다.

        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.
        // 사용자 ID가 null이면 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            // PostService를 통해 이미지 파일들을 업로드하고, 저장된 경로 리스트를 반환받습니다.
            List<String> uploadedPaths = postService.uploadPostImages(files);
            // 업로드된 이미지 경로 리스트와 함께 HTTP 200 OK 응답을 반환합니다.
            return ResponseEntity.ok(Map.of("imagePaths", uploadedPaths));
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
            // 이미지 업로드 중 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "이미지 업로드 중 오류 발생: " + e.getMessage()));
        }
    }

    /**
     * 기존 게시글을 수정합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 게시글 수정 권한을 확인합니다 (게시글 작성자와 일치해야 함).
     *
     * @param postId 수정할 게시글의 고유 ID (경로 변수).
     * @param postDTO 수정된 게시글 정보를 담고 있는 PostDTO 객체 (요청 본문).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 수정된 게시글 정보(PostDTO)와 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PutMapping("/{postId}") // PUT 요청을 "/api/posts/{postId}" 경로로 매핑합니다.
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId,
                                         @RequestBody PostDTO postDTO,
                                         HttpServletRequest request) {
        postDTO.setId(postId); // 경로 변수에서 받은 postId를 PostDTO에 설정합니다.
        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.

        // 사용자 ID가 null이면 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // PostService를 통해 게시글을 수정하고, 수정된 게시글 정보를 반환받습니다.
            PostDTO updatedPost = postService.updatePost(postDTO, userId);
            return ResponseEntity.ok(updatedPost); // HTTP 200 OK 상태 코드와 함께 수정된 게시글 정보를 반환합니다.
        } catch (SecurityException e) {
            // 권한 없음 (SecurityException) 발생 시 HTTP 403 Forbidden 응답과 메시지를 반환합니다.
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            // 게시글을 찾을 수 없을 때 (IllegalArgumentException) HTTP 404 Not Found 응답과 메시지를 반환합니다.
            return new ResponseEntity<>("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
            // 게시글 수정 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 서버 오류 발생");
        }
    }

    /**
     * 특정 게시글을 삭제합니다.
     * JWT 토큰에서 사용자 ID를 추출하여 게시글 삭제 권한을 확인합니다 (게시글 작성자와 일치해야 함).
     *
     * @param postId 삭제할 게시글의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 성공 시 HTTP 204 No Content 응답 또는 오류 메시지와 함께 HTTP 응답을 반환합니다.
     */
    @DeleteMapping("/{postId}") // DELETE 요청을 "/api/posts/{postId}" 경로로 매핑합니다.
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, HttpServletRequest request) {
        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.

        // 사용자 ID가 null이면 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // PostService를 통해 게시글을 삭제합니다.
            postService.deletePost(postId, userId);
            // 성공적으로 삭제되면 HTTP 204 No Content 응답을 반환하고 본문은 비웁니다.
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            // 권한 없음 (SecurityException) 발생 시 HTTP 403 Forbidden 응답과 메시지를 반환합니다.
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            // 게시글을 찾을 수 없을 때 (IllegalArgumentException) HTTP 404 Not Found 응답과 메시지를 반환합니다.
            return new ResponseEntity<>("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
            // 게시글 삭제 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 서버 오류 발생");
        }
    }

    /**
     * 특정 게시글에 대한 좋아요를 토글(추가/취소)합니다.
     * 이 API는 인증이 필요하며, JWT 토큰으로 사용자 ID를 확인합니다.
     *
     * @param postId 좋아요를 토글할 게시글의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 좋아요 상태(`isLiked`)와 갱신된 좋아요 수(`likeCount`)를 포함하는 Map과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/{postId}/like") // POST 요청을 "/api/posts/{postId}/like" 경로로 매핑합니다.
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("postId") Long postId,
                                                         HttpServletRequest request) {
        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.

        // 사용자 ID가 null이면 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }
        try {
            // PostService를 통해 좋아요를 토글하고, 좋아요 상태를 반환받습니다.
            boolean isLiked = postService.toggleLike(postId, userId);
            // 갱신된 좋아요 수를 가져옵니다.
            int likeCount = postService.getLikeCount(postId);
            // 좋아요 상태와 좋아요 수를 포함하는 Map과 함께 HTTP 200 OK 응답을 반환합니다.
            return ResponseEntity.ok(Map.of("isLiked", isLiked, "likeCount", likeCount));
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
            // 좋아요 처리 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "좋아요 처리 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }

    /**
     * 특정 게시글에 대한 북마크를 토글(추가/취소)합니다.
     * 이 API는 인증이 필요하며, JWT 토큰으로 사용자 ID를 확인합니다.
     *
     * @param postId 북마크를 토글할 게시글의 고유 ID (경로 변수).
     * @param request 현재 HTTP 요청 객체 (JWT 토큰 추출용).
     * @return 북마크 상태(`isBookmarked`)를 포함하는 Map과 함께 HTTP 200 OK 응답을 반환합니다.
     */
    @PostMapping("/{postId}/bookmark") // POST 요청을 "/api/posts/{postId}/bookmark" 경로로 매핑합니다.
    public ResponseEntity<Map<String, Boolean>> toggleBookmark(@PathVariable("postId") Long postId,
                                                               HttpServletRequest request) {
        Long userId = getUserIdFromJwtToken(request); // JWT 토큰에서 사용자 ID를 추출합니다.

        // 사용자 ID가 null이면 인증되지 않음 응답을 반환합니다.
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            // PostService를 통해 북마크를 토글하고, 북마크 상태를 반환받습니다.
            boolean isBookmarked = postService.toggleBookmark(postId, userId);
            // 북마크 상태를 포함하는 Map과 함께 HTTP 200 OK 응답을 반환합니다.
            return ResponseEntity.ok(Map.of("isBookmarked", isBookmarked));
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 스택 트레이스를 출력합니다.
            // 북마크 처리 중 기타 예외 발생 시 서버 내부 오류 응답을 반환합니다.
            Map<String, Boolean> errorMap = new HashMap<>();
            errorMap.put("isBookmarked", false); // 오류 시 기본적으로 false 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }

    /**
     * 모든 산의 이름을 조회합니다.
     * 이 API는 인증이 필요하지 않지만, 'X-Client-Type' 헤더를 필수로 요구합니다.
     *
     * @param clientType 요청 헤더의 "X-Client-Type" 값 (예: "web").
     * @param request (옵션) JWT 디버깅을 위해 HttpServletRequest를 주입받을 수 있습니다.
     * @return 산 이름(String) 리스트와 함께 HTTP 200 OK 응답을 반환합니다.
     * 산 목록이 비어있을 경우 HTTP 204 No Content 응답을 반환합니다.
     * "X-Client-Type" 헤더가 없거나 'web'이 아닐 경우 HTTP 400 Bad Request 응답을 반환합니다.
     */
    @GetMapping("/mountains") // GET 요청을 "/api/posts/mountains" 경로로 매핑합니다.
    public ResponseEntity<List<String>> getMountainNames(
            @RequestHeader(value = "X-Client-Type", required = false) String clientType, // "X-Client-Type" 헤더를 추출합니다. (필수 아님으로 설정되었지만, 로직에서 검사)
            HttpServletRequest request) { // HttpServletRequest 주입 (옵션)
        // Long debugUserId = getUserIdFromJwtToken(request); // 디버깅 목적으로 주석 해제하여 현재 로그인된 사용자 ID를 확인할 수 있습니다.
        // System.out.println("PostController: getMountainNames Debug: 현재 userId (인증된 경우): " + debugUserId);

        // "X-Client-Type" 헤더가 없거나 "web"이 아니면 HTTP 400 Bad Request 응답을 반환합니다.
        if (clientType == null || !clientType.equals("web")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // PostService를 통해 모든 산 이름을 가져옵니다.
        List<String> mountainNames = postService.getAllMountainNames();
        if (mountainNames.isEmpty()) {
            // 산 목록이 비어있으면 HTTP 204 No Content 응답을 반환합니다.
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mountainNames); // HTTP 200 OK 상태 코드와 함께 산 이름 목록을 반환합니다.
    }
}