package repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import model.User;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    @Insert("INSERT INTO users (username, email, password, nickname, created_at, updated_at) " +
            "VALUES (#{username}, #{email}, #{password}, #{nickname}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);
}