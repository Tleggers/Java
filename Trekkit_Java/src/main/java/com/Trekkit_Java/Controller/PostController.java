package com.Trekkit_Java.Controller;

import com.Trekkit_Java.DTO.PostDTO;
import com.Trekkit_Java.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

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
    public ResponseEntity<PostDTO> getPost(@PathVariable("postId") int postId) {
        PostDTO post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }
    
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO, @AuthenticationPrincipal Long userId) {
        PostDTO createdPost = postService.createPost(postDTO, userId);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable("postId") int postId, 
                                     @RequestBody PostDTO postDTO, 
                                     @AuthenticationPrincipal Long userId) {
        postDTO.setId(postId);
        try {
            PostDTO updatedPost = postService.updatePost(postDTO, userId);
            return ResponseEntity.ok(updatedPost);
        } catch (SecurityException e) {
            return new ResponseEntity<>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") int postId, @AuthenticationPrincipal Long userId) {
        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return new ResponseEntity<>("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(@PathVariable("postId") int postId, 
                                                       @AuthenticationPrincipal Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isLiked = postService.toggleLike(postId, userId);
        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }
}