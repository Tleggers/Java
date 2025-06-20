package com.Trekkit_Java.DAO;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.Trekkit_Java.DTO.CommentDTO;

@Mapper
public interface CommentDAO {

    // [수정] 파라미터 타입을 Long으로 변경 (이전에 이미 제안되었고, CommentService와 일치시킴)
    List<CommentDTO> selectCommentsByPostId(@Param("postId") Long postId);

    // [수정] 파라미터 타입을 Long으로 변경
    CommentDTO selectCommentById(@Param("id") Long id);

    int insertComment(CommentDTO comment);

    int updateComment(CommentDTO comment);

    // [수정] 파라미터 타입을 Long으로 변경
    int deleteComment(@Param("id") Long id);

    // [수정] 파라미터 타입을 Long으로 변경
    int selectCommentCountByPostId(@Param("postId") Long postId);

    // [수정] 파라미터 타입을 Long으로 변경
    List<CommentDTO> selectCommentsByUserId(@Param("userId") Long userId);
}