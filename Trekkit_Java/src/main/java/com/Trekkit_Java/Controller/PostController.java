package com.Trekkit_Java.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Trekkit_Java.Service.PostService;
import model.Post;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private PostService postService;

    // 글 목록 조회 (최신순/인기순)
    @GetMapping
    public List<Post> getPosts(@RequestParam(required = false, defaultValue = "최신순") String sort) {
        return postService.getPosts(sort);
    }

    // 새 글 작성
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }
}