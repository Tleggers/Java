package repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import model.Post;

@Mapper
public interface PostMapper {
    
    @Select("SELECT * FROM posts ORDER BY created_at DESC")
    List<Post> findAllByOrderByCreatedAtDesc();

    @Select("SELECT * FROM posts ORDER BY view_count DESC")
    List<Post> findAllByOrderByViewCountDesc();

    @Insert("INSERT INTO posts (nickname, title, picture, content, view_count, created_at, updated_at) " +
            "VALUES (#{nickname}, #{title}, #{picture}, #{content}, #{viewCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPost(Post post);
    
    // 추가적인 메서드들 (필요시 사용)
    @Select("SELECT * FROM posts WHERE id = #{id}")
    Post findById(Long id);
    
    @Select("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
    int updateViewCount(Long id);
}