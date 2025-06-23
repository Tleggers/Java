package com.Trekkit_Java.DAO;

import com.Trekkit_Java.DTO.PostDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostDAO {
    // ... 기존 메소드는 동일 ...
    List<PostDTO> findAll(Map<String, Object> params);
    int count(@Param("mountain") String mountain);
    PostDTO findById(@Param("postId") Long postId);
    int save(PostDTO post);
    int update(PostDTO post);
    int delete(@Param("postId") Long postId);
    void increaseViewCount(@Param("postId") Long postId);
    void saveImage(@Param("postId") Long postId, @Param("imagePath") String imagePath);
    List<String> findImagesByPostId(@Param("postId") Long postId);
    void deleteImagesByPostId(@Param("postId") Long postId);
    void addLike(@Param("postId") Long postId, @Param("userId") Long userId);
    void deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);
    int findLikeByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
    void updateLikeCount(@Param("postId") Long postId);
    void updateCommentCount(@Param("postId") Long postId);
    
    // [추가] 현재 좋아요 개수를 직접 조회하는 메소드
    int getLikeCount(@Param("postId") Long postId);

    // [추가] 북마크 관련 DAO 메소드
    void addBookmark(@Param("postId") Long postId, @Param("userId") Long userId);
    void deleteBookmark(@Param("postId") Long postId, @Param("userId") Long userId);
    int findBookmarkByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
    @Select("SELECT name FROM mountain ORDER BY name ASC")
    List<String> findAllMountainNames();
}
