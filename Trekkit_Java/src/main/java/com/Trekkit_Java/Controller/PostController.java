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

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    /**
     * 게시글 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(value = "sort", defaultValue = "최신순") String sort,
            @RequestParam(value = "mountain", required = false) String mountain,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            // 페이지 오프셋 계산
            int offset = page * size;
 
            System.out.println("sort: " + sort);
        	System.out.println("mountain: " + mountain);
        	System.out.println("page: " + page);
        	System.out.println("size: " + size);
        	System.out.println("offset: " + offset);
            
            // 게시글 목록 조회
            List<PostDTO> posts = postService.getPosts(sort, mountain, offset, size);
            
            System.out.println(posts);
            
            // 총 게시글 수 조회
            int totalCount = postService.getPostCount(mountain);
            
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("totalCount", totalCount); 
            response.put("currentPage", page);
            response.put("pageSize", size);
            
            System.out.println(response);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "게시글 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable("id") int id) {
        try {
            // 조회수 증가
            postService.increaseViewCount(id);
            
            // 게시글 조회
            PostDTO post = postService.getPostById(id);
            
            if (post != null) {
                return ResponseEntity.ok(post);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 새 게시글 작성
     */
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO post) {
        try {
            // 게시글 생성
            PostDTO createdPost = postService.createPost(post);
            return ResponseEntity.ok(createdPost);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 이미지 업로드
     */
    @PostMapping("/upload-images")
    public ResponseEntity<Map<String, Object>> uploadImages(
            @RequestParam("images") MultipartFile[] images) {
        
        try {
            List<String> imagePaths = postService.uploadImages(images);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imagePaths", imagePaths);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "이미지 업로드 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 좋아요 토글
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable("postId") int postId,
            @RequestParam("userId") String userId) {
        
        try {
            Map<String, Object> result = postService.toggleLike(postId, userId);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "좋아요 처리 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 북마크 토글
     */
    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable("postId") int postId,
            @RequestParam("userId") String userId) {
        
        try {
            boolean isBookmarked = postService.toggleBookmark(postId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isBookmarked", isBookmarked);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "북마크 처리 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 산 목록 조회
     */
    @GetMapping("/mountains")
    public ResponseEntity<List<String>> getMountains() {
        try {
            List<String> mountains = postService.getMountains();
            return ResponseEntity.ok(mountains);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable("postId") int postId) {
        try {
            boolean success = postService.deletePost(postId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "게시글 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}