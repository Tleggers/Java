package com.Trekkit_Java.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Trekkit_Java.DTO.PostDTO;
import com.Trekkit_Java.Service.PostService;

/**
 * 게시글 관련 REST API를 처리하는 컨트롤러 클래스
 * 
 * 주요 기능:
 * - 게시글 CRUD (생성, 조회, 수정, 삭제)
 * - 이미지 업로드 및 관리
 * - 좋아요/북마크 기능
 * - 게시글 목록 조회 (정렬, 필터링, 페이징)
 * - 산 목록 조회
 * 
 * @author Trekkit Team
 * @version 1.0
 */
@RestController // REST API 컨트롤러임을 나타내는 어노테이션
@RequestMapping("/api/posts") // 모든 메서드의 기본 URL 경로 설정
public class PostController {
    
    /**
     * PostService 의존성 주입
     * 게시글 관련 비즈니스 로직을 처리하는 서비스 클래스
     */
    @Autowired
    private PostService postService;
    
    /**
     * 게시글 목록 조회 API (이미지 정보 포함)
     * 
     * 기능:
     * - 정렬 방식에 따른 게시글 목록 조회 (최신순/인기순)
     * - 산 이름으로 필터링
     * - 페이지네이션 지원
     * - 각 게시글의 이미지 존재 여부 및 개수 정보 포함
     * 
     * @param sort 정렬 방식 (기본값: "최신순", 옵션: "최신순", "인기순")
     * @param mountain 산 이름 필터 (선택사항, null이면 전체 조회)
     * @param page 페이지 번호 (기본값: 0, 0부터 시작)
     * @param size 페이지 크기 (기본값: 10, 한 페이지당 게시글 수)
     * @return ResponseEntity<Map<String, Object>> 게시글 목록과 페이징 정보
     */
    @GetMapping // HTTP GET 요청 처리
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(value = "sort", defaultValue = "최신순") String sort,
            @RequestParam(value = "mountain", required = false) String mountain,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            // 페이지 오프셋 계산 (데이터베이스 LIMIT OFFSET에 사용)
            // 예: page=1, size=10 → offset=10 (11번째부터 조회)
            int offset = page * size;
            
            // 게시글 목록 조회 - 이미지 정보도 함께 조회
            // 기존 getPosts 대신 getPostsWithImageInfo 메서드 사용
            List<PostDTO> posts = postService.getPostsWithImageInfo(sort, mountain, offset, size);
            
            // 필터 조건에 맞는 총 게시글 수 조회 (페이징 계산용)
            int totalCount = postService.getPostCount(mountain);
            
            // 클라이언트에 반환할 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);           // 게시글 목록 (이미지 정보 포함)
            response.put("totalCount", totalCount); // 전체 게시글 수
            response.put("currentPage", page);      // 현재 페이지 번호
            response.put("pageSize", size);         // 페이지 크기
            
            // HTTP 200 OK 상태코드와 함께 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 에러 발생 시 500 Internal Server Error 상태코드와 에러 메시지 반환
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "게시글 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 특정 게시글 상세 조회 API
     * 
     * 기능:
     * - 게시글 ID로 상세 정보 조회
     * - 조회 시 자동으로 조회수 1 증가
     * - 게시글에 첨부된 이미지 목록도 함께 반환
     * 
     * @param id 조회할 게시글 ID (URL 경로에서 추출)
     * @return ResponseEntity<PostDTO> 게시글 상세 정보 또는 404 Not Found
     */
    @GetMapping("/{id}") // URL 경로 변수 사용 (예: /api/posts/123)
    public ResponseEntity<PostDTO> getPost(@PathVariable("id") int id) {
        try {
            // 게시글 조회수 1 증가 (조회할 때마다 자동 증가)
            postService.increaseViewCount(id);
            
            // 게시글 상세 정보 조회 (이미지 경로 목록 포함)
            PostDTO post = postService.getPostById(id);
            
            if (post != null) {
                // 게시글이 존재하면 HTTP 200 OK와 함께 게시글 정보 반환
                return ResponseEntity.ok(post);
            } else {
                // 게시글이 존재하지 않으면 HTTP 404 Not Found 반환
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            // 서버 에러 발생 시 HTTP 500 Internal Server Error 반환
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 새 게시글 작성 API
     * 
     * 기능:
     * - JSON 형태로 전달받은 게시글 정보를 데이터베이스에 저장
     * - 이미지 경로가 포함된 경우 post_images 테이블에도 저장
     * - 생성된 게시글의 ID를 자동으로 생성하여 반환
     * 
     * @param post 작성할 게시글 정보 (JSON → PostDTO 객체로 변환)
     * @return ResponseEntity<PostDTO> 생성된 게시글 정보 또는 500 에러
     */
    @PostMapping // HTTP POST 요청 처리
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO post) {
        try {
            // 게시글 생성 및 데이터베이스에 저장
            // 이미지 경로가 있다면 post_images 테이블에도 함께 저장
            PostDTO createdPost = postService.createPost(post);
            
            // HTTP 200 OK와 함께 생성된 게시글 정보 반환
            return ResponseEntity.ok(createdPost);
            
        } catch (Exception e) {
            // 게시글 생성 실패 시 HTTP 500 Internal Server Error 반환
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 이미지 파일 업로드 API
     * 
     * 기능:
     * - 여러 개의 이미지를 동시에 업로드 가능
     * - 파일 크기 및 확장자 검증
     * - 고유한 파일명 생성으로 중복 방지
     * - 업로드된 파일의 웹 접근 경로 반환
     * 
     * @param images 업로드할 이미지 파일 배열 (multipart/form-data)
     * @return ResponseEntity<Map<String, Object>> 업로드 결과와 이미지 경로 목록
     */
    @PostMapping("/upload-images") // 이미지 업로드 전용 엔드포인트
    public ResponseEntity<Map<String, Object>> uploadImages(
            @RequestParam("images") MultipartFile[] images) {
        
        try {
            // 이미지 파일들을 서버에 저장하고 웹 접근 경로 목록 반환
            // 파일 크기, 확장자 검증 및 고유 파일명 생성 포함
            List<String> imagePaths = postService.uploadImages(images);
            
            // 성공 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);              // 업로드 성공 여부
            response.put("imagePaths", imagePaths);     // 저장된 이미지 경로들
            
            // HTTP 200 OK와 함께 성공 응답 반환
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            // 이미지 업로드 실패 시 에러 응답
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "이미지 업로드 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 게시글 좋아요 토글 API
     * 
     * 기능:
     * - 이미 좋아요를 누른 상태면 좋아요 제거
     * - 좋아요를 누르지 않은 상태면 좋아요 추가
     * - posts 테이블의 like_count 컬럼 자동 업데이트
     * - 현재 좋아요 상태와 총 좋아요 수 반환
     * 
     * @param postId 좋아요를 누를 게시글 ID (URL 경로에서 추출)
     * @param userId 좋아요를 누르는 사용자 ID (쿼리 파라미터)
     * @return ResponseEntity<Map<String, Object>> 좋아요 처리 결과
     */
    @PostMapping("/{postId}/like") // 좋아요 토글 전용 엔드포인트
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable("postId") int postId,
            @RequestParam("userId") String userId) {
        
        try {
            // 좋아요 토글 처리 (추가 또는 제거)
            // 결과: isLiked(현재 상태), likeCount(총 좋아요 수)
            Map<String, Object> result = postService.toggleLike(postId, userId);
            
            // HTTP 200 OK와 함께 처리 결과 반환
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // 좋아요 처리 실패 시 에러 응답
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "좋아요 처리 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 게시글 북마크 토글 API
     * 
     * 기능:
     * - 이미 북마크한 상태면 북마크 제거
     * - 북마크하지 않은 상태면 북마크 추가
     * - 사용자별 북마크 목록 관리
     * 
     * @param postId 북마크할 게시글 ID (URL 경로에서 추출)
     * @param userId 북마크하는 사용자 ID (쿼리 파라미터)
     * @return ResponseEntity<Map<String, Object>> 북마크 처리 결과
     */
    @PostMapping("/{postId}/bookmark") // 북마크 토글 전용 엔드포인트
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable("postId") int postId,
            @RequestParam("userId") String userId) {
        
        try {
            // 북마크 토글 처리 (추가 또는 제거)
            boolean isBookmarked = postService.toggleBookmark(postId, userId);
            
            // 북마크 상태 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("isBookmarked", isBookmarked); // 현재 북마크 상태
            
            // HTTP 200 OK와 함께 처리 결과 반환
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 북마크 처리 실패 시 에러 응답
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "북마크 처리 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 등산 가능한 산 목록 조회 API
     * 
     * 기능:
     * - 게시글에서 사용된 산 이름들을 중복 제거하여 조회
     * - 게시글 작성 시 산 선택 드롭다운에 사용
     * - 알파벳 순으로 정렬하여 반환
     * 
     * @return ResponseEntity<List<String>> 산 이름 목록 또는 500 에러
     */
    @GetMapping("/mountains") // 산 목록 조회 전용 엔드포인트
    public ResponseEntity<List<String>> getMountains() {
        try {
            // 데이터베이스에서 중복 제거된 산 목록 조회
            List<String> mountains = postService.getMountains();
            
            // HTTP 200 OK와 함께 산 목록 반환
            return ResponseEntity.ok(mountains);
            
        } catch (Exception e) {
            // 산 목록 조회 실패 시 HTTP 500 Internal Server Error 반환
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 게시글 삭제 API
     * 
     * 기능:
     * - 게시글 본문 삭제
     * - 관련된 이미지 파일 및 경로 정보 삭제
     * - 관련된 댓글, 좋아요, 북마크 정보 삭제 (CASCADE)
     * - 트랜잭션으로 일관성 보장
     * 
     * @param postId 삭제할 게시글 ID (URL 경로에서 추출)
     * @return ResponseEntity<Map<String, Object>> 삭제 성공 여부
     */
    @DeleteMapping("/{postId}") // HTTP DELETE 요청 처리
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable("postId") int postId) {
        try {
            // 게시글 및 관련 데이터 삭제 처리
            // 이미지, 댓글, 좋아요, 북마크 등 모든 관련 데이터 함께 삭제
            boolean success = postService.deletePost(postId);
            
            // 삭제 결과 응답 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", success); // 삭제 성공 여부
            
            // HTTP 200 OK와 함께 삭제 결과 반환
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 삭제 실패 시 에러 응답
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "게시글 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
