package com.Trekkit_Java.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Trekkit_Java.DAO.CommentDAO;
import com.Trekkit_Java.DAO.PostDAO;
import com.Trekkit_Java.DTO.CommentDTO;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;
    
    @Autowired
    private PostDAO postDAO;
    
    /**
     * 특정 게시글의 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(int postId) {
        try {
            return commentDAO.selectCommentsByPostId(postId);
        } catch (Exception e) {
            throw new RuntimeException("댓글 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 새 댓글 작성
     */
    public CommentDTO createComment(CommentDTO comment) {
        try {
            // 댓글 내용 길이 검증 (200자 제한)
            if (comment.getContent() != null && comment.getContent().length() > 200) {
                throw new IllegalArgumentException("댓글은 200자를 초과할 수 없습니다.");
            }
            
            // 댓글 삽입
            int result = commentDAO.insertComment(comment);
            
            if (result > 0 && comment.getId() != null) {
                // posts 테이블의 comment_count 업데이트
                postDAO.updateCommentCount(comment.getPostId());
                
                // 생성된 댓글 정보 반환
                return commentDAO.selectCommentById(comment.getId());
            }
            
            throw new RuntimeException("댓글 생성에 실패했습니다.");
        } catch (Exception e) {
            throw new RuntimeException("댓글 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 댓글 수정
     */
    public CommentDTO updateComment(CommentDTO comment) {
        try {
            // 댓글 내용 길이 검증
            if (comment.getContent() != null && comment.getContent().length() > 200) {
                throw new IllegalArgumentException("댓글은 200자를 초과할 수 없습니다.");
            }
            
            int result = commentDAO.updateComment(comment);
            
            if (result > 0) {
                return commentDAO.selectCommentById(comment.getId());
            }
            
            throw new RuntimeException("댓글 수정에 실패했습니다.");
        } catch (Exception e) {
            throw new RuntimeException("댓글 수정 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 댓글 삭제
     */
    public boolean deleteComment(int commentId, int postId) {
        try {
            int result = commentDAO.deleteComment(commentId);
            
            if (result > 0) {
                // posts 테이블의 comment_count 업데이트
                postDAO.updateCommentCount(postId);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            throw new RuntimeException("댓글 삭제 중 오류 발생: " + e.getMessage(), e);
        }
    }
    
    /**
     * 특정 게시글의 댓글 수 조회
     */
    @Transactional(readOnly = true)
    public int getCommentCountByPostId(int postId) {
        try {
            return commentDAO.selectCommentCountByPostId(postId);
        } catch (Exception e) {
            throw new RuntimeException("댓글 수 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
}