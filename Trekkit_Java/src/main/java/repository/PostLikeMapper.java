package repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//PostLikeMapper.java
@Mapper
public interface PostLikeMapper {
 @Select("SELECT COUNT(*) > 0 FROM post_likes WHERE post_id = #{postId} AND user_id = #{userId}")
 boolean isLiked(@Param("postId") Long postId, @Param("userId") String userId);
 
 @Insert("INSERT INTO post_likes (post_id, user_id) VALUES (#{postId}, #{userId})")
 int insertLike(@Param("postId") Long postId, @Param("userId") String userId);
 
 @Delete("DELETE FROM post_likes WHERE post_id = #{postId} AND user_id = #{userId}")
 int deleteLike(@Param("postId") Long postId, @Param("userId") String userId);
}