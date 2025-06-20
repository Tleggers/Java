package com.Trekkit_Java.Service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Trekkit_Java.DAO.CommentDAO;
import com.Trekkit_Java.DAO.PostDAO;
import com.Trekkit_Java.DTO.CommentDTO;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentDAO commentDAO;
    private final PostDAO postDAO;

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId) { // int -> Long 수정
        return commentDAO.selectCommentsByPostId(postId);
    }

    public CommentDTO createComment(CommentDTO comment, Long userId) {
        comment.setUserId(userId);
        commentDAO.insertComment(comment);
        postDAO.updateCommentCount(comment.getPostId());
        return commentDAO.selectCommentById(comment.getId());
    }

    public CommentDTO updateComment(CommentDTO comment, Long userId) {
        CommentDTO originalComment = commentDAO.selectCommentById(comment.getId());
        if (originalComment == null) {
            throw new IllegalArgumentException("수정할 댓글이 존재하지 않습니다.");
        }
        if (!Objects.equals(originalComment.getUserId(), userId)) {
            throw new SecurityException("댓글을 수정할 권한이 없습니다.");
        }
        commentDAO.updateComment(comment);
        return commentDAO.selectCommentById(comment.getId());
    }

    public void deleteComment(Long commentId, Long postId, Long userId) { // int -> Long 수정
        CommentDTO originalComment = commentDAO.selectCommentById(commentId);
        if (originalComment == null) {
            throw new IllegalArgumentException("삭제할 댓글이 존재하지 않습니다.");
        }
        if (!Objects.equals(originalComment.getUserId(), userId)) {
            throw new SecurityException("댓글을 삭제할 권한이 없습니다.");
        }

        commentDAO.deleteComment(commentId);
        postDAO.updateCommentCount(postId);
    }
}