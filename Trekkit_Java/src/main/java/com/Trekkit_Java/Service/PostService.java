package com.Trekkit_Java.Service;

import com.Trekkit_Java.DAO.PostDAO;
import com.Trekkit_Java.DTO.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDAO postDAO;

    @Transactional
    public PostDTO createPost(PostDTO postDTO, Long userId) {
        postDTO.setUserId(userId);
        postDAO.save(postDTO);
        if (postDTO.getImagePaths() != null && !postDTO.getImagePaths().isEmpty()) {
            for (String imagePath : postDTO.getImagePaths()) {
                postDAO.saveImage(postDTO.getId(), imagePath);
            }
        }
        return postDTO;
    }

    @Transactional
    public PostDTO getPostById(int postId) {
        postDAO.increaseViewCount(postId);
        PostDTO post = postDAO.findById(postId);
        if (post != null) {
            List<String> images = postDAO.findImagesByPostId(postId);
            post.setImagePaths(images);
        }
        return post;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllPosts(int page, int size, String sort, String mountain) {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", page * size);
        params.put("limit", size);
        params.put("sort", sort);
        params.put("mountain", mountain);
        
        List<PostDTO> posts = postDAO.findAll(params);
        int totalCount = postDAO.count(mountain);
        
        for (PostDTO post : posts) {
            List<String> images = postDAO.findImagesByPostId(post.getId());
            post.setImagePaths(images);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("totalCount", totalCount);
        return response;
    }

    @Transactional
    public PostDTO updatePost(PostDTO postDTO, Long userId) {
        PostDTO originalPost = postDAO.findById(postDTO.getId());
        if (originalPost == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        if (!Objects.equals(originalPost.getUserId(), userId)) {
            throw new SecurityException("게시글을 수정할 권한이 없습니다.");
        }
        postDAO.update(postDTO);
        postDAO.deleteImagesByPostId(postDTO.getId());
        if (postDTO.getImagePaths() != null && !postDTO.getImagePaths().isEmpty()) {
            for (String imagePath : postDTO.getImagePaths()) {
                postDAO.saveImage(postDTO.getId(), imagePath);
            }
        }
        return postDTO;
    }
    
    @Transactional
    public void deletePost(int postId, Long userId) {
        PostDTO post = postDAO.findById(postId);
        if (post == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        if (!Objects.equals(post.getUserId(), userId)) {
            throw new SecurityException("게시글을 삭제할 권한이 없습니다.");
        }
        postDAO.delete(postId);
    }
    
    @Transactional
    public boolean toggleLike(int postId, Long userId) {
        boolean isLiked;
        if (postDAO.findLikeByPostIdAndUserId(postId, userId) > 0) {
            postDAO.deleteLike(postId, userId);
            isLiked = false;
        } else {
            postDAO.addLike(postId, userId);
            isLiked = true;
        }
        postDAO.updateLikeCount(postId);
        return isLiked;
    }
}