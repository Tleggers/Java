package com.Trekkit_Java.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Trekkit_Java.Service.FileUploadService;
import com.Trekkit_Java.Service.PostService;

import model.Post;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*") // Flutter 앱과의 CORS 허용
public class PostController {

    @Autowired
    private PostService postService;
    
    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 게시글 목록 조회 (필터링 지원)
     * @param sort 정렬 방식 (최신순/인기순)
     * @param mountain 산 필터
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(required = false, defaultValue = "최신순") String sort,
            @RequestParam(required = false) String mountain,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        List<Post> posts = postService.getPosts(sort, mountain, page, size);
        int totalCount = postService.getTotalCount(mountain);
        
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("totalCount", totalCount);
        response.put("currentPage", page);
        response.put("pageSize", size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post != null) {
            postService.incrementViewCount(id); // 조회수 증가
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 새 게시글 작성
     */
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    /**
     * 이미지 업로드
     */
    @PostMapping("/upload-images")
    public ResponseEntity<Map<String, Object>> uploadImages(
            @RequestParam("images") MultipartFile[] images) {
        
        try {
            List<String> imagePaths = fileUploadService.uploadImages(images);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imagePaths", imagePaths);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 좋아요 토글
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            @RequestParam String userId) {
        
        boolean isLiked = postService.toggleLike(id, userId);
        int likeCount = postService.getLikeCount(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        response.put("likeCount", likeCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 북마크 토글
     */
    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable Long id,
            @RequestParam String userId) {
        
        boolean isBookmarked = postService.toggleBookmark(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 산 목록 조회 (필터용)
     */
    @GetMapping("/mountains")
    public ResponseEntity<List<String>> getMountains() {
        List<String> mountains = postService.getAllMountains();
        return ResponseEntity.ok(mountains);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boolean deleted = postService.deletePost(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}