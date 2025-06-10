package com.Trekkit_Java.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.DTO.CommentDTO;
import com.Trekkit_Java.Service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 특정 게시글의 댓글 목록 조회
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable("postId") int postId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 새 댓글 작성
     */
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO comment) {
        try {
            CommentDTO createdComment = commentService.createComment(comment);
            return ResponseEntity.ok(createdComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable("commentId") int commentId,
            @RequestBody CommentDTO comment) {
        try {
            comment.setId(commentId);
            CommentDTO updatedComment = commentService.updateComment(comment);
            return ResponseEntity.ok(updatedComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}/post/{postId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable("commentId") int commentId,
            @PathVariable("postId") int postId) {
        try {
            boolean success = commentService.deleteComment(commentId, postId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "댓글 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}