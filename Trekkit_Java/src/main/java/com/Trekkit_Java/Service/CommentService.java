package com.Trekkit_Java.Service;

import java.util.List; // List 인터페이스 사용
import java.util.Objects; // 객체 비교 유틸리티 사용 (Objects.equals)

import org.springframework.stereotype.Service; // 이 클래스가 서비스 계층의 컴포넌트임을 나타내는 어노테이션
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위한 어노테이션
import com.Trekkit_Java.DAO.CommentDAO; // CommentDAO 인터페이스 임포트 (데이터베이스 접근)
import com.Trekkit_Java.DAO.PostDAO; // PostDAO 인터페이스 임포트 (게시글 관련 데이터베이스 접근)
import com.Trekkit_Java.DTO.CommentDTO; // CommentDTO 데이터 전송 객체 임포트
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor 어노테이션 (final 필드 생성자 자동 생성)

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * DAO(Data Access Object)를 통해 데이터베이스와 상호작용하며, 트랜잭션 관리를 포함합니다.
 */
@Service // 이 클래스가 Spring 서비스 계층의 컴포넌트임을 나타냅니다.
@Transactional // 이 클래스의 모든 퍼블릭 메서드에 트랜잭션 기능을 적용합니다. (데이터 일관성 보장)
@RequiredArgsConstructor // Lombok: final 필드(commentDAO, postDAO)를 주입하는 생성자를 자동으로 생성
public class CommentService {

    private final CommentDAO commentDAO; // CommentDAO를 주입받아 댓글 관련 DB 작업을 수행합니다.
    private final PostDAO postDAO; // PostDAO를 주입받아 게시글 관련 DB 작업을 수행합니다. (예: 댓글 수 업데이트)

    /**
     * 특정 게시글의 모든 댓글 목록을 조회합니다.
     * 읽기 전용 트랜잭션으로 설정하여 성능을 최적화합니다.
     * @param postId 댓글을 조회할 게시글의 고유 ID.
     * @return 해당 게시글의 댓글 목록 (List<CommentDTO>).
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentDAO.selectCommentsByPostId(postId); // DAO를 통해 댓글 목록을 가져와 반환합니다.
    }

    /**
     * 새로운 댓글을 생성하고 데이터베이스에 삽입합니다.
     * 댓글 생성 후 해당 게시글의 댓글 수를 1 증가시킵니다.
     * @param comment 생성할 댓글 정보 (CommentDTO 객체).
     * @param userId 댓글 작성자의 ID.
     * @return 생성된 댓글 정보 (CommentDTO 객체, DB에서 자동 생성된 ID 포함).
     */
    public CommentDTO createComment(CommentDTO comment, Long userId) {
        comment.setUserId(userId); // 댓글 DTO에 작성자 ID를 설정합니다.
        commentDAO.insertComment(comment); // 댓글을 DB에 삽입합니다.
        postDAO.updateCommentCount(comment.getPostId()); // 해당 게시글의 댓글 수를 1 증가시킵니다.
        return commentDAO.selectCommentById(comment.getId()); // 삽입된 댓글의 상세 정보를 조회하여 반환합니다.
    }

    /**
     * 기존 댓글의 내용을 수정합니다.
     * 댓글을 수정하려는 사용자가 해당 댓글의 작성자인지 확인하여 권한을 검사합니다.
     * @param comment 수정할 댓글 정보 (CommentDTO 객체, ID를 포함해야 함).
     * @param userId 댓글 수정을 요청한 사용자의 ID.
     * @return 수정된 댓글 정보 (CommentDTO 객체).
     * @throws IllegalArgumentException 수정할 댓글이 존재하지 않을 경우 발생.
     * @throws SecurityException 댓글을 수정할 권한이 없을 경우 발생.
     */
    public CommentDTO updateComment(CommentDTO comment, Long userId) {
        CommentDTO originalComment = commentDAO.selectCommentById(comment.getId()); // 기존 댓글 정보를 조회합니다.
        if (originalComment == null) {
            // 수정할 댓글이 존재하지 않으면 예외를 던집니다.
            throw new IllegalArgumentException("수정할 댓글이 존재하지 않습니다.");
        }
        // 현재 사용자의 ID와 원본 댓글의 작성자 ID가 다르면 권한 없음 예외를 던집니다.
        if (!Objects.equals(originalComment.getUserId(), userId)) {
            throw new SecurityException("댓글을 수정할 권한이 없습니다.");
        }
        commentDAO.updateComment(comment); // 댓글을 DB에서 업데이트합니다.
        return commentDAO.selectCommentById(comment.getId()); // 업데이트된 댓글의 상세 정보를 조회하여 반환합니다.
    }

    /**
     * 특정 댓글을 삭제합니다.
     * 댓글을 삭제하려는 사용자가 해당 댓글의 작성자인지 확인하여 권한을 검사합니다.
     * 댓글 삭제 후 해당 게시글의 댓글 수를 1 감소시킵니다.
     * @param commentId 삭제할 댓글의 고유 ID.
     * @param postId 댓글이 속한 게시글의 고유 ID (댓글 수 업데이트용).
     * @param userId 댓글 삭제를 요청한 사용자의 ID.
     * @throws IllegalArgumentException 삭제할 댓글이 존재하지 않을 경우 발생.
     * @throws SecurityException 댓글을 삭제할 권한이 없을 경우 발생.
     */
    public void deleteComment(Long commentId, Long postId, Long userId) {
        CommentDTO originalComment = commentDAO.selectCommentById(commentId); // 기존 댓글 정보를 조회합니다.
        if (originalComment == null) {
            // 삭제할 댓글이 존재하지 않으면 예외를 던집니다.
            throw new IllegalArgumentException("삭제할 댓글이 존재하지 않습니다.");
        }
        // 현재 사용자의 ID와 원본 댓글의 작성자 ID가 다르면 권한 없음 예외를 던집니다.
        if (!Objects.equals(originalComment.getUserId(), userId)) {
            throw new SecurityException("댓글을 삭제할 권한이 없습니다.");
        }

        commentDAO.deleteComment(commentId); // 댓글을 DB에서 삭제합니다.
        postDAO.updateCommentCount(postId); // 해당 게시글의 댓글 수를 1 감소시킵니다.
    }
}
