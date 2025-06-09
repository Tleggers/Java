package repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//PostBookmarkMapper.java
@Mapper
public interface PostBookmarkMapper {
 @Select("SELECT COUNT(*) > 0 FROM post_bookmarks WHERE post_id = #{postId} AND user_id = #{userId}")
 boolean isBookmarked(@Param("postId") Long postId, @Param("userId") String userId);
 
 @Insert("INSERT INTO post_bookmarks (post_id, user_id) VALUES (#{postId}, #{userId})")
 int insertBookmark(@Param("postId") Long postId, @Param("userId") String userId);
 
 @Delete("DELETE FROM post_bookmarks WHERE post_id = #{postId} AND user_id = #{userId}")
 int deleteBookmark(@Param("postId") Long postId, @Param("userId") String userId);
}