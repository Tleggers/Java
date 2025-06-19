package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.PostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostDAO {
    List<PostDTO> findAll(Map<String, Object> params);
    int count(@Param("mountain") String mountain);
    PostDTO findById(@Param("postId") int postId);
    int save(PostDTO post);
    int update(PostDTO post);
    int delete(@Param("postId") int postId);
    void increaseViewCount(@Param("postId") int postId);
    void saveImage(@Param("postId") int postId, @Param("imagePath") String imagePath);
    List<String> findImagesByPostId(@Param("postId") int postId);
    void deleteImagesByPostId(@Param("postId") int postId);
    void addLike(@Param("postId") int postId, @Param("userId") Long userId);
    void deleteLike(@Param("postId") int postId, @Param("userId") Long userId);
    int findLikeByPostIdAndUserId(@Param("postId") int postId, @Param("userId") Long userId);
    void updateLikeCount(@Param("postId") int postId);
    void updateCommentCount(@Param("postId") int postId);
}