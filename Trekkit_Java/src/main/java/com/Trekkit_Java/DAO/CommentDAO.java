package com.Trekkit_Java.DAO;

import java.util.List; // List 인터페이스 사용을 위한 임포트
import org.apache.ibatis.annotations.Mapper; // MyBatis의 Mapper 어노테이션 임포트
import org.apache.ibatis.annotations.Param; // MyBatis의 @Param 어노테이션 임포트 (SQL 쿼리 파라미터 매핑)
import com.Trekkit_Java.DTO.CommentDTO; // CommentDTO 데이터 전송 객체 임포트

/**
 * 댓글 관련 데이터베이스 작업을 위한 MyBatis Mapper 인터페이스입니다.
 * 이 인터페이스의 메서드들은 `CommentMapper.xml` 파일의 SQL 쿼리와 매핑됩니다.
 */
@Mapper // 이 인터페이스가 MyBatis의 매퍼임을 선언합니다.
public interface CommentDAO {

    /**
     * 특정 게시글의 모든 댓글을 조회합니다.
     *
     * @param postId 댓글을 조회할 게시글의 고유 ID.
     * @return 해당 게시글의 댓글 목록 (CommentDTO 리스트).
     */
    List<CommentDTO> selectCommentsByPostId(@Param("postId") Long postId);

    /**
     * 특정 ID를 가진 댓글을 조회합니다.
     *
     * @param id 조회할 댓글의 고유 ID.
     * @return 조회된 댓글 (CommentDTO 객체) 또는 null.
     */
    CommentDTO selectCommentById(@Param("id") Long id);

    /**
     * 새로운 댓글을 데이터베이스에 삽입합니다.
     *
     * @param comment 삽입할 댓글 정보 (CommentDTO 객체).
     * @return 삽입된 레코드의 수.
     */
    int insertComment(CommentDTO comment);

    /**
     * 기존 댓글을 수정합니다.
     *
     * @param comment 수정할 댓글 정보 (CommentDTO 객체, ID를 포함해야 함).
     * @return 수정된 레코드의 수.
     */
    int updateComment(CommentDTO comment);

    /**
     * 특정 ID를 가진 댓글을 데이터베이스에서 삭제합니다.
     *
     * @param id 삭제할 댓글의 고유 ID.
     * @return 삭제된 레코드의 수.
     */
    int deleteComment(@Param("id") Long id);

    /**
     * 특정 게시글에 달린 댓글의 총 개수를 조회합니다.
     *
     * @param postId 댓글 개수를 조회할 게시글의 고유 ID.
     * @return 해당 게시글의 댓글 총 개수.
     */
    int selectCommentCountByPostId(@Param("postId") Long postId);

    /**
     * 특정 사용자가 작성한 모든 댓글을 조회합니다.
     *
     * @param userId 댓글을 조회할 사용자의 고유 ID.
     * @return 해당 사용자가 작성한 댓글 목록 (CommentDTO 리스트).
     */
    List<CommentDTO> selectCommentsByUserId(@Param("userId") Long userId);
}