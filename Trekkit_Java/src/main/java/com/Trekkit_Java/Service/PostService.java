package com.Trekkit_Java.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import model.Post;
import repository.PostMapper;

@Service
public class PostService {
    
    @Autowired
    private PostMapper postMapper;

    public List<Post> getPosts(String sort) {
        if ("인기순".equals(sort)) {
            return postMapper.findAllByOrderByViewCountDesc();
        }
        return postMapper.findAllByOrderByCreatedAtDesc();
    }

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViewCount(0); // 초기 조회수 0으로 설정
        
        postMapper.insertPost(post);
        return post;
    }
}