package repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import model.Post;

@Mapper
public interface PostMapper {

    // 최신순 조회 (필터링 및 페이징)
    @Select("<script>" +
            "SELECT * FROM posts " +
            "<where>" +
            "<if test='mountain != null and mountain != \"\"'>" +
            "AND mountain = #{mountain}" +
            "</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<Post> findAllByOrderByCreatedAtDesc(@Param("mountain") String mountain, 
                                           @Param("offset") int offset, 
                                           @Param("size") int size);

    // 인기순 조회 (필터링 및 페이징)
    @Select("<script>" +
            "SELECT * FROM posts " +
            "<where>" +
            "<if test='mountain != null and mountain != \"\"'>" +
            "AND mountain = #{mountain}" +
            "</if>" +
            "</where>" +
            "ORDER BY view_count DESC, created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<Post> findAllByOrderByViewCountDesc(@Param("mountain") String mountain, 
                                           @Param("offset") int offset, 
                                           @Param("size") int size);

    // 전체 게시글 수 조회
    @Select("<script>" +
            "SELECT COUNT(*) FROM posts " +
            "<where>" +
            "<if test='mountain != null and mountain != \"\"'>" +
            "AND mountain = #{mountain}" +
            "</if>" +
            "</where>" +
            "</script>")
    int getTotalCount(@Param("mountain") String mountain);

    // 게시글 상세 조회
    @Select("SELECT * FROM posts WHERE id = #{id}")
    Post findById(Long id);

    // 게시글 작성
    @Insert("INSERT INTO posts (nickname, title, mountain, content, image_paths, view_count, like_count, comment_count, created_at, updated_at) " +
            "VALUES (#{nickname}, #{title}, #{mountain}, #{content}, #{imagePaths,typeHandler=com.Trekkit_Java.Config.ListTypeHandler}, " +
            "#{viewCount}, #{likeCount}, #{commentCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPost(Post post);

    // 조회수 증가
    @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
    int updateViewCount(Long id);

    // 좋아요 수 증가
    @Update("UPDATE posts SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    // 좋아요 수 감소
    @Update("UPDATE posts SET like_count = like_count - 1 WHERE id = #{id}")
    int decrementLikeCount(Long id);

    // 좋아요 수 조회
    @Select("SELECT like_count FROM posts WHERE id = #{id}")
    int getLikeCount(Long id);

    // 댓글 수 업데이트
    @Update("UPDATE posts SET comment_count = #{count} WHERE id = #{id}")
    int updateCommentCount(@Param("id") Long id, @Param("count") int count);

    // 모든 산 목록 조회
    @Select("SELECT DISTINCT mountain FROM posts WHERE mountain IS NOT NULL ORDER BY mountain")
    List<String> findAllMountains();

    // 게시글 삭제
    @Delete("DELETE FROM posts WHERE id = #{id}")
    int deletePost(Long id);

    // 게시글 수정
    @Update("UPDATE posts SET title = #{title}, mountain = #{mountain}, content = #{content}, " +
            "image_paths = #{imagePaths,typeHandler=com.Trekkit_Java.Config.ListTypeHandler}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updatePost(Post post);
}
