package com.Trekkit_Java.DAO;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.Trekkit_Java.DTO.CommentDTO;

@Mapper
public interface CommentDAO {
    
    /**
     * 특정 게시글의 댓글 목록 조회
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    List<CommentDTO> selectCommentsByPostId(@Param("postId") int postId);
    
    /**
     * 댓글 상세 조회
     * @param id 댓글 ID
     * @return 댓글 정보
     */
    CommentDTO selectCommentById(@Param("id") int id);
    
    /**
     * 새 댓글 삽입
     * @param comment 댓글 정보
     * @return 삽입된 행 수
     */
    int insertComment(CommentDTO comment);
    
    /**
     * 댓글 수정
     * @param comment 수정할 댓글 정보
     * @return 수정된 행 수
     */
    int updateComment(CommentDTO comment);
    
    /**
     * 댓글 삭제
     * @param id 댓글 ID
     * @return 삭제된 행 수
     */
    int deleteComment(@Param("id") int id);
    
    /**
     * 특정 게시글의 댓글 수 조회
     * @param postId 게시글 ID
     * @return 댓글 수
     */
    int selectCommentCountByPostId(@Param("postId") int postId);
    
    /**
     * 특정 사용자의 댓글 목록 조회
     * @param userId 사용자 ID
     * @return 댓글 목록
     */
    List<CommentDTO> selectCommentsByUserId(@Param("userId") String userId);
}